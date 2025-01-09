package com.supergroup.kos.building.domain.service.building;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetStorageBuildingCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.StorageType;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.building.CastleBuilding;
import com.supergroup.kos.building.domain.model.building.StoneMineBuilding;
import com.supergroup.kos.building.domain.model.building.StorageBuilding;
import com.supergroup.kos.building.domain.model.building.WoodMineBuilding;
import com.supergroup.kos.building.domain.model.config.BaseBuildingConfig;
import com.supergroup.kos.building.domain.model.config.CastleConfig;
import com.supergroup.kos.building.domain.model.mining.PeopleAndGoldMiningResult;
import com.supergroup.kos.building.domain.model.mining.PeopleAndGoldMiningSnapshot;
import com.supergroup.kos.building.domain.model.point.Point;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.building.CastleBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.StoneMineBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.WoodMineBuildingRepository;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.mining.PeopleAndGoldMiningService;
import com.supergroup.kos.building.domain.service.point.PointService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

import lombok.experimental.Delegate;

@Service
public class CastleBuildingService extends BaseBuildingService {

    @Delegate
    private final CastleBuildingRepository    castleBuildingRepository;
    private final PointService                pointService;
    private final BuildingConfigDataSource    buildingConfigDataSource;
    private final AssetsService               assetsService;
    private final PeopleAndGoldMiningService  peopleAndGoldMiningService;
    private final StoneMineBuildingRepository stoneMineBuildingRepository;
    private final WoodMineBuildingRepository  woodMineBuildingRepository;
    private final StorageBuildingService      storageBuildingService;

    @Autowired
    public CastleBuildingService(CastleBuildingRepository castleBuildingRepository,
                                 PointService pointService,
                                 BuildingConfigDataSource buildingConfigDataSource,
                                 AssetsService assetsService,
                                 PeopleAndGoldMiningService peopleAndGoldMiningService,
                                 StoneMineBuildingRepository stoneMineBuildingRepository,
                                 WoodMineBuildingRepository woodMineBuildingRepository,
                                 StorageBuildingService storageBuildingService,
                                 KosProfileService kosProfileService) {
        super(kosProfileService, buildingConfigDataSource);
        this.castleBuildingRepository = castleBuildingRepository;
        this.pointService = pointService;
        this.buildingConfigDataSource = buildingConfigDataSource;
        this.assetsService = assetsService;
        this.peopleAndGoldMiningService = peopleAndGoldMiningService;
        this.stoneMineBuildingRepository = stoneMineBuildingRepository;
        this.woodMineBuildingRepository = woodMineBuildingRepository;
        this.storageBuildingService = storageBuildingService;
    }

    public CastleBuilding getCastleBuilding(KosProfileCommand command) {
        return castleBuildingRepository.findByKosProfile_Id(command.getKosProfileId()).orElseThrow(
                () -> KOSException.of(ErrorCode.CASTLE_BUILDING_NOT_FOUND));
    }

    public CastleBuilding getCastleBuildingDetail(KosProfileCommand command) {
        var castleBuilding = getCastleBuilding(command);
        var castleConfig = (CastleConfig) buildingConfigDataSource.getConfig(BuildingName.CASTLE, castleBuilding.getLevel());
        castleBuilding.setMaxPopulation(getMaxPopulation(castleConfig));
        castleBuilding.setGoldProduction(getGoldProduction(castleBuilding, castleConfig));
        castleBuilding.setMpMultiplier(castleConfig.getMpMultiplier());
        castleBuilding.setPeopleProduction(getPeopleProduction(castleBuilding, castleConfig));
        return castleBuilding;
    }

    private Double getPeopleProduction(CastleBuilding castleBuilding, CastleConfig castleConfig) {
        var threshold = pointService.getKosPoint(castleBuilding.getKosProfile()).getMpPoint() * castleConfig.getMpMultiplier();
        var populationThresholdRound = (long) Math.ceil(threshold) + 1L;
        var idlePeople = Math.floor(castleBuilding.getIdlePeople());
        if (idlePeople <= populationThresholdRound) {
            return (populationThresholdRound * castleConfig.getPopulationGrowthBase()) / castleConfig.getMaxPopulation();
        }
        return castleConfig.getPopulationGrowthBase() / (idlePeople - threshold);
    }

    private Double getGoldProduction(CastleBuilding castleBuilding, CastleConfig castleConfig) {
        Double currentGoldPerPerson = getCurrentGoldPerPerson(castleConfig,
                                                              castleBuilding.getKosProfile());
        return Math.floor(castleBuilding.getIdlePeople()) * currentGoldPerPerson;
    }

    public Double getCurrentGoldPerPerson(CastleConfig config, KosProfile kosProfile) {
        return config.getGoldPerPerson() * (1 + kosProfile.getBonusGoldProductionPercent());
    }

    public Double getCurrentPopulationGrowth(CastleConfig config) {
        return config.getPopulationGrowthBase();
    }

    public Long getMaxPopulation(CastleConfig config) {
        return config.getMaxPopulation();
    }

    @Transactional
    public PeopleAndGoldMiningResult claimPeopleAndGold(Long kosProfileId) {
        CastleBuilding building = castleBuildingRepository.findByKosProfile_Id(kosProfileId)
                                                          .orElseThrow(() -> KOSException.of(ErrorCode.CASTLE_BUILDING_NOT_FOUND));
        CastleConfig config = (CastleConfig) buildingConfigDataSource.getConfig(BuildingName.CASTLE, building.getLevel());
        Point point = pointService.getKosPoint(building.getKosProfile());

        PeopleAndGoldMiningSnapshot snapshot = mapToSnapshot(building, config, point.getMpPoint(), calculatePeopleInWork(kosProfileId));
        PeopleAndGoldMiningResult peopleAndGoldMiningResult = peopleAndGoldMiningService.getMiningClaim(snapshot);
        StorageBuilding goldStorageBuilding = storageBuildingService.getBuilding(
                new GetStorageBuildingCommand(StorageType.GOLD, kosProfileId).setCheckValidUnlock(false));
        return updateAfterClaim(peopleAndGoldMiningResult, building, goldStorageBuilding);
    }

    @Transactional
    public void updateIdlePeople(Long kosProfileId, Long diffPeople) { // people idle increase or decrease
        claimPeopleAndGold(kosProfileId);
        CastleBuilding building = castleBuildingRepository.findByKosProfile_Id(kosProfileId)
                                                          .orElseThrow(() -> KOSException.of(ErrorCode.CASTLE_BUILDING_NOT_FOUND));
        building.setIdlePeople(building.getIdlePeople() + diffPeople);
        castleBuildingRepository.save(building);
    }

    public PeopleAndGoldMiningSnapshot mapToSnapshot(CastleBuilding building, CastleConfig config, Long mp, Long peopleInWork) {
        PeopleAndGoldMiningSnapshot snapshot = new PeopleAndGoldMiningSnapshot();
        snapshot.setMp(mp)
                .setIdlePeople(building.getIdlePeople())
                .setGoldPerPerson(getCurrentGoldPerPerson(config, building.getKosProfile()))
                .setMpMultiplier(config.getMpMultiplier())
                .setPopulationGrowthBase(config.getPopulationGrowthBase())
                .setMaxPopulation(config.getMaxPopulation())
                .setPeopleInWork(peopleInWork)
                .setLastTimeClaim(building.getLastTimeClaim());
        return snapshot;
    }

    public PeopleAndGoldMiningResult updateAfterClaim(PeopleAndGoldMiningResult result, CastleBuilding building,
                                                      StorageBuilding goldStorageBuilding) {
        Assets assets = assetsService.getAssets(new KosProfileCommand().setKosProfileId(building.getKosProfile().getId()));
        var currentGold = assets.getGold() + result.getIncreaseGold();
        assets.setGold(currentGold);
        building.setIdlePeople(building.getIdlePeople() + result.getIncreasePeople())
                .setLastTimeClaim(result.getLastTimeClaim());
        assetsService.save(assets);
        castleBuildingRepository.save(building);
        PeopleAndGoldMiningResult peopleAndGoldMiningResult = new PeopleAndGoldMiningResult();
        peopleAndGoldMiningResult.setIncreaseGold(result.getIncreaseGold())
                                 .setTotalGold(assets.getGold())
                                 .setIncreasePeople(result.getIncreasePeople())
                                 .setTotalPeople(building.getIdlePeople())
                                 .setLastTimeClaim(result.getLastTimeClaim());
        return peopleAndGoldMiningResult;
    }

    public Long calculatePeopleInWork(Long kosProfileId) {
        StoneMineBuilding stoneMineBuilding = stoneMineBuildingRepository.findByKosProfileId(kosProfileId).orElseThrow(
                () -> KOSException.of(ErrorCode.STONE_MINE_BUILDING_IS_NOT_FOUND));
        WoodMineBuilding woodMineBuilding = woodMineBuildingRepository.findByKosProfileId(kosProfileId).orElseThrow(
                () -> KOSException.of(ErrorCode.WOOD_MINE_BUILDING_IS_NOT_FOUND));
        return stoneMineBuilding.getWorker() + woodMineBuilding.getWorker();

    }

    public CastleBuilding save(CastleBuilding castleBuilding) {
        return castleBuildingRepository.save(castleBuilding);
    }

    @Override
    protected BaseBuildingConfig getBuildingConfig(Long level) {
        return buildingConfigDataSource.getConfig(BuildingName.CASTLE, level);
    }
}
