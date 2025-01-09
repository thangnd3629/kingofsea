package com.supergroup.kos.building.domain.service.building;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetBuildingInfoCommand;
import com.supergroup.kos.building.domain.command.GetStorageBuildingCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.StorageType;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.building.CastleBuilding;
import com.supergroup.kos.building.domain.model.building.StoneMineBuilding;
import com.supergroup.kos.building.domain.model.building.StorageBuilding;
import com.supergroup.kos.building.domain.model.config.BaseBuildingConfig;
import com.supergroup.kos.building.domain.model.config.StoneMineConfig;
import com.supergroup.kos.building.domain.model.mining.StoneMiningResult;
import com.supergroup.kos.building.domain.model.mining.StoneMiningSnapshot;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.building.CastleBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.StoneMineBuildingRepository;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.config.KosConfigService;
import com.supergroup.kos.building.domain.service.mining.StoneMiningService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.technology.TechnologyService;

import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StoneMineService extends BaseBuildingService {
    @Delegate
    private final StoneMineBuildingRepository stoneMineBuildingRepository;
    private final StoneMiningService          stoneMiningService;
    private final CastleBuildingRepository    castleBuildingRepository;
    private final AssetsService               assetsService;
    private final CastleBuildingService       castleBuildingService;
    private final StorageBuildingService      storageBuildingService;
    private final TechnologyService           technologyService;
    private final KosConfigService            kosConfigService;

    public StoneMineService(@Autowired KosProfileService kosProfileService,
                            @Autowired BuildingConfigDataSource buildingConfigDataSource,
                            StoneMineBuildingRepository stoneMineBuildingRepository,
                            StoneMiningService stoneMiningService,
                            CastleBuildingRepository castleBuildingRepository,
                            AssetsService assetsService,
                            CastleBuildingService castleBuildingService,
                            StorageBuildingService storageBuildingService,
                            TechnologyService technologyService,
                            KosConfigService kosConfigService) {
        super(kosProfileService, buildingConfigDataSource);
        this.stoneMineBuildingRepository = stoneMineBuildingRepository;
        this.stoneMiningService = stoneMiningService;
        this.castleBuildingRepository = castleBuildingRepository;
        this.assetsService = assetsService;
        this.castleBuildingService = castleBuildingService;
        this.storageBuildingService = storageBuildingService;
        this.technologyService = technologyService;
        this.kosConfigService = kosConfigService;
    }

    /**
     * Get building info
     */
    @Transactional
    public StoneMineBuilding getBuildingInfo(GetBuildingInfoCommand command) {
        var building = stoneMineBuildingRepository.findByKosProfileId(command.getKosProfileId())
                                                  .orElseThrow(() -> KOSException.of(ErrorCode.STONE_MINE_BUILDING_IS_NOT_FOUND));

        if (Objects.isNull(command.getCheckValidUnlock()) || command.getCheckValidUnlock()) {building.validUnlockBuilding(technologyService);}

        var config = (StoneMineConfig) buildingConfigDataSource.getConfig(building.getName(), building.getLevel());
        Double currentSpeedPerWorker = getCurrentSpeedPerWorker(config, building.getKosProfile());
        building.setProduction(building.getWorker() * currentSpeedPerWorker);
        building.setCurrentSpeedPerWorker(currentSpeedPerWorker);
        return building;
    }

    public Double getCurrentSpeedPerWorker(StoneMineConfig config, KosProfile kosProfile) {
        var production = config.getStonePerWorker() * (1 + kosProfile.getBonusStoneProductionPercent());
        // if base is occupied, decrease it
        if (kosProfile.getBase().isOccupied()) {
            var occupyEffect = kosConfigService.occupyEffect();
            production -= (production * occupyEffect.getDecreaseWoodExploit());
        }
        return production;
    }

    /**
     * claim wood in mine
     */
    public StoneMiningResult claimStone(Long kosProfileId) {
        log.info("Claim stone " + kosProfileId);
        StorageBuilding storageBuilding = storageBuildingService.getBuilding(
                new GetStorageBuildingCommand(StorageType.STONE, kosProfileId).setCheckValidUnlock(false));
        StoneMineBuilding building = findByKosProfileId(kosProfileId)
                .orElseThrow(() -> KOSException.of(ErrorCode.WOOD_MINE_BUILDING_IS_NOT_FOUND));

        building.validUnlockBuilding(technologyService);

        if (storageBuilding.getAmount() >= storageBuilding.getCapacity()) {
            building.setLastTimeClaim(LocalDateTime.now());
            stoneMineBuildingRepository.save(building);
            return miningResultWhenMaxCapacity(storageBuilding.getCapacity());
        }
        StoneMineConfig config = (StoneMineConfig) buildingConfigDataSource.getConfig(BuildingName.STONE_MINE, building.getLevel());

        var stoneMiningResult = stoneMiningService.getMiningClaim(mapToSnapshot(building, config, storageBuilding));
        var asset = updateAfterClaim(stoneMiningResult, building, kosProfileId);
        stoneMiningResult.setTotal(asset.getStone());
        return stoneMiningResult;
    }

    /**
     * Change worker in mine
     */
    @Transactional
    public void changeWorker(Long kosProfileId, Long worker) { // worker mining new
        StoneMineBuilding building = findByKosProfileId(kosProfileId)
                .orElseThrow(() -> KOSException.of(ErrorCode.STONE_MINE_BUILDING_IS_NOT_FOUND));

        building.validUnlockBuilding(technologyService);

        StoneMineConfig config = (StoneMineConfig) buildingConfigDataSource.getConfig(BuildingName.STONE_MINE, building.getLevel());

        if (worker > config.getMaxWorker()) {
            throw KOSException.of(ErrorCode.EXCEED_THE_ALLOWED_NUMBER_OF_PEOPLE);
        }

        CastleBuilding castleBuilding = castleBuildingRepository.findByKosProfile_Id(kosProfileId)
                                                                .orElseThrow(() -> KOSException.of(ErrorCode.CASTLE_BUILDING_NOT_FOUND));
        // check enough idlePeople
        if (Objects.isNull(building.getWorker())) {
            building.setWorker(0L);
        }
        Long workerChange = worker - building.getWorker();
        if (workerChange > 0) {
            Double idlePeople = castleBuilding.getIdlePeople();
            if (idlePeople < workerChange) {
                throw KOSException.of(ErrorCode.NOT_ENOUGH_IDLE_POPULATION);
            }
        }
        // changePeopleIdle
        castleBuildingService.updateIdlePeople(kosProfileId, workerChange * -1);

        // claim stone
        claimStone(kosProfileId);

        // update and save to db
        building.setWorker(worker);
    }

    @Override
    protected BaseBuildingConfig getBuildingConfig(Long level) {
        return buildingConfigDataSource.getConfig(BuildingName.STONE_MINE, level);
    }

    private StoneMiningSnapshot mapToSnapshot(StoneMineBuilding building, StoneMineConfig config, StorageBuilding storageBuilding) {
        StoneMiningSnapshot snapshot = new StoneMiningSnapshot();
        snapshot.setWorker(building.getWorker())
                .setStonePerWorker(getCurrentSpeedPerWorker(config, building.getKosProfile()))
                .setStone(storageBuilding.getAmount())
                .setCapacity(storageBuilding.getCapacity())
                .setLastTimeClaim(building.getLastTimeClaim());
        return snapshot;
    }

    public Assets updateAfterClaim(StoneMiningResult stoneMiningResult, StoneMineBuilding building, Long kosProfileId) {
        var assets = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfileId));
        assets.setStone(assets.getStone() + stoneMiningResult.getIncrease());

        building.setLastTimeClaim(stoneMiningResult.getLastTimeClaim());
        stoneMiningResult.setTotal(assets.getStone());
        assetsService.save(assets);
        stoneMineBuildingRepository.save(building);
        return assets;
    }

    public StoneMiningResult miningResultWhenMaxCapacity(Long capacity) {
        StoneMiningResult miningResult = new StoneMiningResult();
        miningResult.setTotal(Double.valueOf(capacity));
        miningResult.setIncrease(0.0D);
        miningResult.setLastTimeClaim(LocalDateTime.now());
        return miningResult;
    }
}
