package com.supergroup.kos;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.auth.domain.model.User;
import com.supergroup.auth.domain.repository.persistence.UserRepository;
import com.supergroup.kos.building.domain.command.SaveOrUpdateElementCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.StorageType;
import com.supergroup.kos.building.domain.constant.UpgradeType;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.building.CastleBuilding;
import com.supergroup.kos.building.domain.model.building.StoneMineBuilding;
import com.supergroup.kos.building.domain.model.building.StorageBuilding;
import com.supergroup.kos.building.domain.model.building.StorageBuildingConfig;
import com.supergroup.kos.building.domain.model.building.WoodMineBuilding;
import com.supergroup.kos.building.domain.model.config.CastleConfig;
import com.supergroup.kos.building.domain.model.config.seamap.SeaElementConfig;
import com.supergroup.kos.building.domain.model.config.seamap.UserBaseConfig;
import com.supergroup.kos.building.domain.model.point.Point;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.model.upgrade.InfoInstanceModel;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.repository.persistence.asset.AssetsRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.CastleBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.CastleConfigRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.StoneMineBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.StorageBuildingConfigRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.StorageBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.WoodMineBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.point.PointRepository;
import com.supergroup.kos.building.domain.repository.persistence.profile.KosProfileRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementConfigRepository;
import com.supergroup.kos.building.domain.service.seamap.MapService;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;
import com.supergroup.kos.building.domain.service.upgrade.UpgradeService;

@SpringBootTest(properties = { "seamap.parcel-size=40", "seamap.size=2000" })
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@Transactional
public class CastleBuildingUpgradingTest {

    @Autowired
    private UpgradeService                               upgradeService;
    @Autowired
    private KosProfileRepository                         kosProfileRepository;
    @Autowired
    private BuildingConfigDataSource                     buildingConfigDataSource;
    @Autowired
    private PointRepository                              pointRepository;
    @Autowired
    private UserRepository                               userRepository;
    @Autowired
    private BuildingRepository                           buildingRepository;
    @Autowired
    private CastleBuildingRepository                     castleBuildingRepository;
    @Autowired
    private MapService                                   mapService;
    @Autowired
    private CastleConfigRepository                       castleConfigRepository;
    @Autowired
    private SeaElementConfigRepository<SeaElementConfig> seaElementConfigRepository;
    @Autowired
    private StoneMineBuildingRepository                  stoneMineBuildingRepository;
    @Autowired
    private WoodMineBuildingRepository                   woodMineBuildingRepository;
    @Autowired
    private StorageBuildingRepository                    storageBuildingRepository;
    @Autowired
    private StorageBuildingConfigRepository              storageBuildingConfigRepository;
    @Autowired
    private AssetsRepository                             assetsRepository;
    @Autowired
    private SeaElementService                            seaElementService;

    @Test
    public void test_upgrade_castle() {

        var kosProfile = new KosProfile();
        kosProfile.setId(1L);
        var user = new User().setId(1L).setEmail("test@gmail.com").setPassword("123qweA!").setOriginEmail("test@gmail.com");
        user = userRepository.save(user);
        kosProfile.setUser(user);
        kosProfile = kosProfileRepository.save(kosProfile);

        var asset = new Assets();
        asset.setKosProfile(kosProfile)
             .setGold(1000D)
             .setStone(1000D)
             .setWood(1000D);
        assetsRepository.save(asset);

        var castleBuilding = new CastleBuilding();
        castleBuilding.setLevel(1L);
        castleBuilding.setName(BuildingName.CASTLE);
        castleBuilding.setKosProfile(kosProfile);
        castleBuilding = castleBuildingRepository.save(castleBuilding);

        var stoneMineBuilding = new StoneMineBuilding();
        stoneMineBuilding.setWorker(0L)
                         .setKosProfile(kosProfile)
                         .setName(BuildingName.STONE_MINE);
        stoneMineBuildingRepository.save(stoneMineBuilding);

        var woodMineBuilding = new WoodMineBuilding();
        woodMineBuilding.setWorker(0L)
                        .setKosProfile(kosProfile)
                        .setName(BuildingName.WOOD_MINE);
        woodMineBuildingRepository.save(woodMineBuilding);

        pointRepository.save(new Point().setGpPoint(1000L)
                                        .setMpPoint(1000L)
                                        .setTpPoint(1000L)
                                        .setKosProfile(kosProfile));

        var userBaseConfig = new UserBaseConfig().setName("USER");
        userBaseConfig = seaElementConfigRepository.save(userBaseConfig);

        var castleConfigLevel1 = new CastleConfig();
        castleConfigLevel1.setMpMultiplier(0D)
                          .setGoldPerPerson(0D)
                          .setMaxPopulation(0L)
                          .setPopulationGrowthBase(0D)
                          .setLevel(1L)
                          .setName(BuildingName.CASTLE)
                          .setGpPointReward(0L)
                          .setGold(0L);
        var castleConfigLevel2 = new CastleConfig();
        castleConfigLevel2.setMpMultiplier(0D)
                          .setGoldPerPerson(0D)
                          .setMaxPopulation(0L)
                          .setPopulationGrowthBase(0D)
                          .setLevel(2L)
                          .setGpPointReward(0L)
                          .setName(BuildingName.CASTLE)
                          .setGold(0L);
        castleConfigRepository.save(castleConfigLevel1);
        castleConfigRepository.save(castleConfigLevel2);

        var woodStorage = new StorageBuilding().setStorageType(StorageType.WOOD)
                                               .setCapacity(0L);
        woodStorage.setKosProfile(kosProfile).setName(BuildingName.STORAGE_WOOD).setLevel(1L);
        var stoneStorage = new StorageBuilding().setStorageType(StorageType.STONE)
                                                .setCapacity(0L);
        stoneStorage.setKosProfile(kosProfile).setName(BuildingName.STORAGE_STONE).setLevel(1L);
        var goldStorage = new StorageBuilding().setStorageType(StorageType.GOLD)
                                               .setCapacity(0L);
        goldStorage.setKosProfile(kosProfile).setName(BuildingName.STORAGE_GOLD).setLevel(1L);
        storageBuildingRepository.save(woodStorage);
        storageBuildingRepository.save(stoneStorage);
        storageBuildingRepository.save(goldStorage);

        var goldStorageConfig = new StorageBuildingConfig();
        goldStorageConfig.setCapacity(0D)
                         .setType(StorageType.GOLD)
                         .setName(BuildingName.STORAGE_GOLD)
                         .setLevel(1L);

        var stoneStorageConfig = new StorageBuildingConfig();
        stoneStorageConfig.setCapacity(0D)
                          .setType(StorageType.STONE)
                          .setName(BuildingName.STORAGE_STONE)
                          .setLevel(1L);

        var woodStorageConfig = new StorageBuildingConfig();
        woodStorageConfig.setCapacity(0D)
                         .setType(StorageType.WOOD)
                         .setName(BuildingName.STORAGE_WOOD)
                         .setLevel(1L);
        storageBuildingConfigRepository.save(goldStorageConfig);
        storageBuildingConfigRepository.save(stoneStorageConfig);
        storageBuildingConfigRepository.save(woodStorageConfig);

        var upgradeSession = new UpgradeSession();
        upgradeSession.setDuration(1000L)
                      .setKosProfile(kosProfile)
                      .setTimeStart(LocalDateTime.now().plusDays(1))
                      .setInfoInstanceModel(new InfoInstanceModel().setType(UpgradeType.BUILDING)
                                                                   .setBuildingName(BuildingName.CASTLE)
                                                                   .setInstanceId(castleBuilding.getId()));

        mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(new UserBase().setKosProfile(kosProfile)
                                                                                    .setSeaElementConfig(userBaseConfig)
                                                                                    .setCoordinate(new Coordinates().setX(0L).setY(0L))));

        upgradeService.completeUpgradeBuilding(upgradeSession);
        var userbase = seaElementService.findUserBaseByKosProfileIdFromDatabase(kosProfile.getId());
        Assertions.assertEquals(2, userbase.getKosProfile().getLevel());
    }
}
