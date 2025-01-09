package com.supergroup.kos.building.domain.service.building;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetBuildingInfoCommand;
import com.supergroup.kos.building.domain.command.GetStorageBuildingCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.StorageType;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.building.CastleBuilding;
import com.supergroup.kos.building.domain.model.building.StorageBuilding;
import com.supergroup.kos.building.domain.model.building.WoodMineBuilding;
import com.supergroup.kos.building.domain.model.config.BaseBuildingConfig;
import com.supergroup.kos.building.domain.model.config.WoodMineConfig;
import com.supergroup.kos.building.domain.model.mining.WoodMiningResult;
import com.supergroup.kos.building.domain.model.mining.WoodMiningSnapshot;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.building.CastleBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.WoodMineBuildingRepository;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.config.KosConfigService;
import com.supergroup.kos.building.domain.service.mining.WoodMiningService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.technology.TechnologyService;

import lombok.experimental.Delegate;

@Service
public class WoodMineService extends BaseBuildingService {
    @Delegate
    private final WoodMineBuildingRepository woodMineBuildingRepository;
    private final WoodMiningService          woodMiningService;
    private final CastleBuildingRepository   castleBuildingRepository;
    private final AssetsService              assetsService;
    private final CastleBuildingService      castleBuildingService;
    private final StorageBuildingService     storageBuildingService;
    private final TechnologyService          technologyService;
    private final KosConfigService           kosConfigService;

    public WoodMineService(@Autowired KosProfileService kosProfileService,
                           @Autowired BuildingConfigDataSource buildingConfigDataSource,
                           WoodMineBuildingRepository woodMineBuildingRepository,
                           WoodMiningService woodMiningService,
                           CastleBuildingRepository castleBuildingRepository,
                           AssetsService assetsService,
                           CastleBuildingService castleBuildingService,
                           StorageBuildingService storageBuildingService,
                           TechnologyService technologyService,
                           KosConfigService kosConfigService) {
        super(kosProfileService, buildingConfigDataSource);
        this.woodMineBuildingRepository = woodMineBuildingRepository;
        this.woodMiningService = woodMiningService;
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
    public WoodMineBuilding getBuildingInfo(GetBuildingInfoCommand command) {
        var building = woodMineBuildingRepository.findByKosProfileId(command.getKosProfileId())
                                                 .orElseThrow(() -> KOSException.of(ErrorCode.WOOD_MINE_BUILDING_IS_NOT_FOUND));

        if (Objects.isNull(command.getCheckValidUnlock()) || command.getCheckValidUnlock()) {building.validUnlockBuilding(technologyService);}

        var config = (WoodMineConfig) buildingConfigDataSource.getConfig(building.getName(), building.getLevel());
        Double currentSpeedPerWorker = getCurrentSpeedPerWorker(config, building.getKosProfile());
        // wood production equal worker * speed * (1 + bonus Percent)
        building.setProduction(building.getWorker() * currentSpeedPerWorker);
        // current SpeedPerWorker
        building.setCurrentSpeedPerWorker(currentSpeedPerWorker);
        return building;
    }

    public Double getCurrentSpeedPerWorker(WoodMineConfig config, KosProfile kosProfile) {
        var production = config.getWoodPerWorker() * (1 + kosProfile.getBonusWoodProductionPercent());
        // if base is occupied, decrease it
        if (kosProfile.getBase().isOccupied()) {
            var occupyEffect = kosConfigService.occupyEffect();
            production -= (production * occupyEffect.getDecreaseWoodExploit());
        }
        return production;
    }

    public WoodMiningResult claimWood(Long kosProfileId) {
        StorageBuilding storageBuilding = storageBuildingService.getBuilding(
                new GetStorageBuildingCommand(StorageType.WOOD, kosProfileId).setCheckValidUnlock(false));
        WoodMineBuilding building = findByKosProfileId(kosProfileId)
                .orElseThrow(() -> KOSException.of(ErrorCode.WOOD_MINE_BUILDING_IS_NOT_FOUND));

        building.validUnlockBuilding(technologyService);

        if (storageBuilding.getAmount() >= storageBuilding.getCapacity()) {
            building.setLastTimeClaim(LocalDateTime.now());
            woodMineBuildingRepository.save(building);
            return miningResultWhenMaxCapacity(storageBuilding.getCapacity());
        }
        WoodMineConfig config = (WoodMineConfig) buildingConfigDataSource.getConfig(BuildingName.WOOD_MINE, building.getLevel());
        WoodMiningResult woodMiningResult = woodMiningService.getMiningClaim(mapToSnapshot(building, config, storageBuilding));
        var asset = updateAfterClaim(woodMiningResult, building, kosProfileId);
        woodMiningResult.setTotal(asset.getWood());
        return woodMiningResult;
    }

    @Transactional
    public void changeWorker(Long kosProfileId, Long worker) { // worker mining new
        WoodMineBuilding building = findByKosProfileId(kosProfileId)
                .orElseThrow(() -> KOSException.of(ErrorCode.WOOD_MINE_BUILDING_IS_NOT_FOUND));

        building.validUnlockBuilding(technologyService);

        WoodMineConfig config = (WoodMineConfig) buildingConfigDataSource.getConfig(BuildingName.WOOD_MINE, building.getLevel());
        if (worker > config.getMaxWorker()) {
            throw KOSException.of(ErrorCode.EXCEED_THE_ALLOWED_NUMBER_OF_PEOPLE);
        }

        CastleBuilding castleBuilding = castleBuildingRepository.findByKosProfile_Id(kosProfileId)
                                                                .orElseThrow(() -> KOSException.of(ErrorCode.CASTLE_BUILDING_NOT_FOUND));

        // check enough idlePeople
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
        WoodMiningResult woodMiningResult = claimWood(kosProfileId);

        // update and save to db
        building.setWorker(worker);
    }

    @Transactional
    public void updateLevel(Long kosProfileId, Long newLevel) {
        WoodMineBuilding building = findByKosProfileId(kosProfileId)
                .orElseThrow(() -> KOSException.of(ErrorCode.WOOD_MINE_BUILDING_IS_NOT_FOUND));
        WoodMineConfig config = (WoodMineConfig) buildingConfigDataSource.getConfig(BuildingName.WOOD_MINE, building.getLevel());

        claimWood(kosProfileId);
        building.setLevel(newLevel);
    }

    @Override
    protected BaseBuildingConfig getBuildingConfig(Long level) {
        return buildingConfigDataSource.getConfig(BuildingName.WOOD_MINE, level);
    }

    public WoodMiningSnapshot mapToSnapshot(WoodMineBuilding building, WoodMineConfig config, StorageBuilding storageBuilding) {
        WoodMiningSnapshot snapshot = new WoodMiningSnapshot();
        snapshot.setWorker(building.getWorker())
                .setWoodPerWorker(getCurrentSpeedPerWorker(config, building.getKosProfile()))
                .setWood(storageBuilding.getAmount())
                .setCapacity(storageBuilding.getCapacity())
                .setLastTimeClaim(building.getLastTimeClaim());
        return snapshot;
    }

    public Assets updateAfterClaim(WoodMiningResult woodMiningResult, WoodMineBuilding building, Long kosProfileId) {
        Assets assets = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfileId));
        assets.setWood(assets.getWood() + woodMiningResult.getIncrease());
        building.setLastTimeClaim(woodMiningResult.getLastTimeClaim());
        assetsService.save(assets);
        woodMineBuildingRepository.save(building);
        return assets;
    }

    public WoodMiningResult miningResultWhenMaxCapacity(Long capacity) {
        WoodMiningResult miningResult = new WoodMiningResult();
        miningResult.setTotal(Double.valueOf(capacity));
        miningResult.setIncrease(0.0D);
        miningResult.setLastTimeClaim(LocalDateTime.now());
        return miningResult;
    }
}
