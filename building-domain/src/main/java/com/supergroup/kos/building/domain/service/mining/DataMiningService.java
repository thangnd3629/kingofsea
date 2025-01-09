package com.supergroup.kos.building.domain.service.mining;

import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetBuildingInfoCommand;
import com.supergroup.kos.building.domain.command.GetDataMiningCommand;
import com.supergroup.kos.building.domain.command.GetStorageBuildingCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.StorageType;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.building.CastleBuilding;
import com.supergroup.kos.building.domain.model.building.StoneMineBuilding;
import com.supergroup.kos.building.domain.model.building.StorageBuilding;
import com.supergroup.kos.building.domain.model.building.WoodMineBuilding;
import com.supergroup.kos.building.domain.model.config.CastleConfig;
import com.supergroup.kos.building.domain.model.mining.DataMining;
import com.supergroup.kos.building.domain.model.mining.DataMiningPeopleAndGold;
import com.supergroup.kos.building.domain.model.mining.DataMiningResource;
import com.supergroup.kos.building.domain.model.point.Point;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.building.CastleBuildingRepository;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.CastleBuildingService;
import com.supergroup.kos.building.domain.service.building.StoneMineService;
import com.supergroup.kos.building.domain.service.building.StorageBuildingService;
import com.supergroup.kos.building.domain.service.building.WoodMineService;
import com.supergroup.kos.building.domain.service.point.PointService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DataMiningService {
    private final CastleBuildingService    castleBuildingService;
    private final CastleBuildingRepository castleBuildingRepository;
    private final PointService             pointService;
    private final BuildingConfigDataSource buildingConfigDataSource;
    private final StorageBuildingService   storageBuildingService;
    private final KosProfileService        kosProfileService;
    private final AssetsService            assetsService;
    private final WoodMineService          woodMineService;
    private final StoneMineService         stoneMineService;

    public DataMining getDataMiningService(GetDataMiningCommand command) {
        KosProfile kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(command.getUseId()));
        DataMining dataMining = new DataMining();
        Assets assets = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
        switch (command.getMiningType()) {
            case ALL:
                dataMining.setMiningWood(getDataMiningWood(kosProfile).setCurrentResources(assets.getWood().longValue()));
                dataMining.setMiningStone(getDataMiningStone(kosProfile).setCurrentResources(assets.getStone().longValue()));
                dataMining.setMiningPeopleAndGold(getDataMiningPeopleAndGold(kosProfile).setCurrentGold(assets.getGold().longValue()));
                break;
            case PEOPLE_GOLD:
                dataMining.setMiningPeopleAndGold(getDataMiningPeopleAndGold(kosProfile).setCurrentGold(assets.getGold().longValue()));
                break;
            case WOOD:
                dataMining.setMiningWood(getDataMiningWood(kosProfile).setCurrentResources(assets.getWood().longValue()));
                break;
            case STONE:
                dataMining.setMiningStone(getDataMiningStone(kosProfile).setCurrentResources(assets.getStone().longValue()));
                break;
            default:
                throw KOSException.of(ErrorCode.BAD_REQUEST_ERROR);
        }
        return dataMining;
    }

    private DataMiningPeopleAndGold getDataMiningPeopleAndGold(KosProfile kosProfile) {
        Long kosProfileId = kosProfile.getId();
        DataMiningPeopleAndGold data = new DataMiningPeopleAndGold();
        CastleBuilding building = castleBuildingRepository.findByKosProfile_Id(kosProfileId)
                                                          .orElseThrow(() -> KOSException.of(ErrorCode.CASTLE_BUILDING_NOT_FOUND));
        CastleConfig config = (CastleConfig) buildingConfigDataSource.getConfig(BuildingName.CASTLE, building.getLevel());
        Point point = pointService.getKosPoint(kosProfile);

        StorageBuilding goldStorageBuilding = getStorageBuilding(new GetStorageBuildingCommand(StorageType.GOLD, kosProfileId));

        data.setGoldPerPerson(castleBuildingService.getCurrentGoldPerPerson(config, kosProfile))
            .setMpMultiplier(config.getMpMultiplier())
            .setPopulationGrowthBase(castleBuildingService.getCurrentPopulationGrowth(config))
            .setMaxPopulation(castleBuildingService.getMaxPopulation(config))
            .setPeopleInWork(castleBuildingService.calculatePeopleInWork(kosProfileId))
            .setIdlePeople(building.getIdlePeople().longValue())
            .setMaxGold(goldStorageBuilding.getCapacity())
            .setMp(point.getMpPoint());

        return data;
    }

    private DataMiningResource getDataMiningWood(KosProfile kosProfile) {
        Long kosProfileId = kosProfile.getId();
        DataMiningResource data = new DataMiningResource();
        WoodMineBuilding building = woodMineService.getBuildingInfo(new GetBuildingInfoCommand(kosProfileId));
        StorageBuilding storageBuilding = getStorageBuilding(new GetStorageBuildingCommand(StorageType.WOOD, kosProfileId));
        data.setWorker(building.getWorker())
            .setCapacity(storageBuilding.getCapacity())
            .setSpeedPerWorker(building.getCurrentSpeedPerWorker());
        return data;
    }

    private DataMiningResource getDataMiningStone(KosProfile kosProfile) {
        Long kosProfileId = kosProfile.getId();
        DataMiningResource data = new DataMiningResource();
        StoneMineBuilding building = stoneMineService.getBuildingInfo(new GetBuildingInfoCommand(kosProfileId));
        StorageBuilding storageBuilding = getStorageBuilding(new GetStorageBuildingCommand(StorageType.STONE, kosProfileId));
        data.setWorker(building.getWorker())
            .setCapacity(storageBuilding.getCapacity())
            .setSpeedPerWorker(building.getCurrentSpeedPerWorker());
        return data;
    }

    private StorageBuilding getStorageBuilding(GetStorageBuildingCommand command) {
        StorageBuilding storageBuilding = new StorageBuilding();
        try {
            storageBuilding = storageBuildingService.getBuilding(command);
        } catch (Exception e) {
            if(e instanceof KOSException && ((KOSException) e).getCode().equals(ErrorCode.BUILDING_IS_LOCKED)) {
                storageBuilding.setCapacity(0L);
            }
        }
        return storageBuilding;
    }

}
