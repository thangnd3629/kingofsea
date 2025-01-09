package com.supergroup.kos.building.domain.service.profile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.supergroup.auth.domain.model.User;
import com.supergroup.auth.domain.model.UserProfile;
import com.supergroup.auth.domain.service.UserProfileService;
import com.supergroup.auth.domain.service.UserService;
import com.supergroup.core.constant.ConfigKey;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.core.model.Config;
import com.supergroup.core.provider.ConfigProvider;
import com.supergroup.kos.building.domain.command.SaveOrUpdateElementCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.EscortShipGroupLevel;
import com.supergroup.kos.building.domain.constant.StorageType;
import com.supergroup.kos.building.domain.constant.TechnologyType;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.building.ArmoryBuilding;
import com.supergroup.kos.building.domain.model.building.CastleBuilding;
import com.supergroup.kos.building.domain.model.building.CommandBuilding;
import com.supergroup.kos.building.domain.model.building.CommunityBuilding;
import com.supergroup.kos.building.domain.model.building.LighthouseBuilding;
import com.supergroup.kos.building.domain.model.building.MilitaryBuilding;
import com.supergroup.kos.building.domain.model.building.StoneMineBuilding;
import com.supergroup.kos.building.domain.model.building.StorageBuilding;
import com.supergroup.kos.building.domain.model.building.VaultBuilding;
import com.supergroup.kos.building.domain.model.building.WoodMineBuilding;
import com.supergroup.kos.building.domain.model.config.InitAssetKosConfig;
import com.supergroup.kos.building.domain.model.mining.QueenBuilding;
import com.supergroup.kos.building.domain.model.mining.ResearchBuilding;
import com.supergroup.kos.building.domain.model.mining.ScoutBuilding;
import com.supergroup.kos.building.domain.model.point.Point;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.KosWarInfo;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.model.ship.EscortShip;
import com.supergroup.kos.building.domain.model.ship.EscortShipGroup;
import com.supergroup.kos.building.domain.model.technology.UserTechnology;
import com.supergroup.kos.building.domain.repository.persistence.asset.AssetsRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.ArmoryBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.CastleBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.CommandBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.CommunityBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.LighthouseBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.MilitaryBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.QueenBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.ResearchBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.ScoutBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.StoneMineBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.StorageBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.VaultBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.WoodMineBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.point.PointRepository;
import com.supergroup.kos.building.domain.repository.persistence.profile.KosProfileRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.KosWarInfoRepository;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipGroupConfigLevelDataSource;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipGroupRepository;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipRepository;
import com.supergroup.kos.building.domain.repository.persistence.technology.TechnologyRepository;
import com.supergroup.kos.building.domain.service.seamap.MapService;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KosProfileService {
    @Delegate
    private final KosProfileRepository                 kosProfileRepository;
    private final UserService                          userService;
    private final BuildingRepository                   buildingRepository;
    private final PointRepository                      pointRepository;
    private final AssetsRepository                     assetsRepository;
    private final CastleBuildingRepository             castleBuildingRepository;
    private final ResearchBuildingRepository           researchBuildingRepository;
    private final WoodMineBuildingRepository           woodMineBuildingRepository;
    private final StoneMineBuildingRepository          stoneMineBuildingRepository;
    private final StorageBuildingRepository            storageBuildingRepository;
    private final VaultBuildingRepository              vaultBuildingRepository;
    private final QueenBuildingRepository              queenBuildingRepository;
    private final CommunityBuildingRepository          communityBuildingRepository;
    private final ArmoryBuildingRepository             armoryBuildingRepository;
    private final EscortShipGroupRepository            escortShipGroupRepository;
    private final EscortShipRepository                 escortShipRepository;
    private final EscortShipConfigDataSource           escortShipConfigDataSource;
    private final EscortShipGroupConfigLevelDataSource escortShipGroupConfigLevelDataSource;
    private final MilitaryBuildingRepository           militaryBuildingRepository;
    private final CommandBuildingRepository            commandBuildingRepository;
    private final ConfigProvider                       configProvider;
    private final TechnologyRepository                 technologyRepository;
    private final LighthouseBuildingRepository         lightHouseBuildingRepository;
    private final Gson                                 gson;
    private final ScoutBuildingRepository              scoutBuildingRepository;
    private final UserBaseService                      userBaseService;
    private final MapService                           mapService;
    private final UserProfileService                   userProfileService;

    private final KosWarInfoRepository kosWarInfoRepository;

    public KosProfile getKosProfile(UserCommand userCommand) {
        return kosProfileRepository.findByUserId(userCommand.getUserId())
                                   .orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND));
    }

    /**
     * Get game level
     */
    public Long getLevel(Long id) {
        return castleBuildingRepository.findByKosProfile_Id(id)
                                       .orElseThrow(() -> KOSException.of(ErrorCode.CAN_NOT_GET_LEVEL))
                                       .getLevel();
    }

    public KosProfile getKosProfileById(Long id) {
        return kosProfileRepository.findById(id).orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND));
    }

    public KosProfile saveProfile(KosProfile kosProfile) {
        return kosProfileRepository.save(kosProfile);
    }

    @Transactional
    public KosProfile createNewProfile(UserCommand userCommand) {

        User user = userService.findById(userCommand.getUserId()).orElseThrow(() -> KOSException.of(ErrorCode.USER_NOT_FOUND));

        var kosProfile = kosProfileRepository.findByUserId(user.getId()).orElse(new KosProfile());
        if (kosProfile.getId() == null) {
            kosProfile.setUser(user);
            kosProfile = kosProfileRepository.save(kosProfile);
        }
        LocalDateTime now = LocalDateTime.now();

        // init value default for kos profile
        initDefaultValue(kosProfile);

        var initConfig = getInitAssetConfig();
        // point
        initPoint(kosProfile, initConfig);

        // init asset
        initAsset(kosProfile, initConfig);

        // castle
        initCastleBuilding(kosProfile, initConfig);

        // research building
        initResearchBuilding(kosProfile);

        // wood
        initWoodMine(kosProfile);

        // stone
        initStoneMine(kosProfile);

        // storage
        initStorageBuilding(kosProfile);

        // Vault Building
        initVaultBuilding(kosProfile);

        // Queen Building
        initQueenBuilding(kosProfile);

        // Community Building
        initCommunityBuilding(kosProfile);

        // Armory Building
        initArmoryBuilding(kosProfile);

        // Command Building
        initCommandPortBuilding(kosProfile);

        // Military Building
        initMilitaryBuilding(kosProfile);

        // scout Building
        initScoutBuilding(kosProfile);
        // Escort Ship Group, Escort ship
        initEscortShip(kosProfile);
        // Base User
        initBaseUser(kosProfile);
        initLighthouseBuilding(kosProfile);
        initKosWarInfo(kosProfile);

        /* save all */
        return kosProfileRepository.save(kosProfile);
    }

    public void initKosWarInfo(KosProfile kosProfile) {
        if (kosWarInfoRepository.existsByKosProfile_Id(kosProfile.getId())) {
            return;
        }
        KosWarInfo warInfo = new KosWarInfo().setKosProfile(kosProfile);
        kosWarInfoRepository.save(warInfo);
    }

    private void initAsset(KosProfile kosProfile, InitAssetKosConfig initAsset) {
        if (Objects.isNull(kosProfile.getId()) || !assetsRepository.existsByKosProfileId(kosProfile.getId())) {
            // asset
            var kosAsset = new Assets().setKosProfile(kosProfile)
                                       .setGold(initAsset.getGold())
                                       .setStone(initAsset.getStone())
                                       .setWood(initAsset.getWood());
            assetsRepository.save(kosAsset);
        }
    }

    private void initPoint(KosProfile kosProfile, InitAssetKosConfig initAsset) {
        if (Objects.isNull(kosProfile.getId()) || !pointRepository.existsByKosProfileId(kosProfile.getId())) {
            var kosPoint = new Point().setKosProfile(kosProfile)
                                      .setGpPoint(initAsset.getGp())
                                      .setTpPoint(initAsset.getTp())
                                      .setMpPoint(initAsset.getMp());
            pointRepository.save(kosPoint);
        }
    }

    private void initCastleBuilding(KosProfile kosProfile, InitAssetKosConfig initAsset) {
        var now = LocalDateTime.now();
        if (Objects.isNull(kosProfile.getId())
            || !castleBuildingRepository.existsByKosProfileId(kosProfile.getId())) {
            var castle = new CastleBuilding();
            castle.setIdlePeople(initAsset.getPeople())
                  .setLastTimeClaim(now)
                  .setKosProfile(kosProfile)
                  .setLevel(initAsset.getCastleLevel())
                  .setName(BuildingName.CASTLE);
            kosProfile.setCastleBuilding(castle);
            buildingRepository.save(castle);
        }
    }

    /**
     * Init ship group for user
     */
    /** Init Escort ship group and escort shp type */
    private void initEscortShip(KosProfile kosProfile) {
        if (Objects.isNull(kosProfile.getId()) || !escortShipGroupRepository.existsByKosProfileId(kosProfile.getId())) {
            var escortShipGroupDefaultConfigs = escortShipGroupConfigLevelDataSource.getByGroupConfigLevel(EscortShipGroupLevel.WOOD);
            if (escortShipGroupDefaultConfigs.size() != 3) {
                log.error("Escort ship group level wrong");
                return;
            }
            Assets asset = assetsRepository.findByKosProfile_Id(kosProfile.getId())
                                           .orElseThrow(() -> KOSException.of(ErrorCode.KOS_ASSETS_NOT_FOUND));
            var escortShipGroups = escortShipGroupDefaultConfigs.stream().map(
                                                                        config -> new EscortShipGroup().setAssets(asset).setEscortShipGroupLevelConfig(config))
                                                                .collect(Collectors.toList());
            var escortShips = escortShipRepository.findByKosProfileId(kosProfile.getId());
            if (escortShips.isEmpty()) {
                var escortShipConfigs = escortShipConfigDataSource.getAll();
                var escortShipsInit = escortShipConfigs.stream().map(config -> {
                    var escortShipGroupName = config.getEscortShipGroupConfig().getName();
                    var groups = escortShipGroups.stream()
                                                 .filter(e -> e.getEscortShipGroupLevelConfig().getEscortShipGroupConfig().getName()
                                                               .equals(escortShipGroupName)).collect(Collectors.toList());
                    return new EscortShip()
                            .setEscortShipConfig(config)
                            .setEscortShipGroup(groups.get(0)) // TODO: FIX HERE something is wrong when get element 0
                            .setPercentRssBuild(1.0)
                            .setPercentSpeedBuild(1.0)
                            .setMaxLevel(1L)
                            .setLevel(1L).setAmount(0L);
                }).collect(Collectors.toList());
                escortShipRepository.saveAll(escortShipsInit);
            }
            escortShipGroupRepository.saveAll(escortShipGroups);
        }
    }

    /**
     * Community Building will be locked
     */
    private void initCommunityBuilding(KosProfile kosProfile) {
        if (Objects.isNull(kosProfile.getId()) || !communityBuildingRepository.existsByKosProfileId(kosProfile.getId())) {
            var communityBuilding = new CommunityBuilding();
            communityBuilding
                    .setMaxListingRelic(1L)
                    .setLevel(1L)
                    .setIsLock(true)
                    .setKosProfile(kosProfile)
                    .setName(BuildingName.COMMUNITY);
            buildingRepository.save(communityBuilding);
        }
    }

    /**
     * Military will be locked
     */
    private void initMilitaryBuilding(KosProfile kosProfile) {
        if (Objects.isNull(kosProfile.getId()) || !militaryBuildingRepository.existsByKosProfileId(kosProfile.getId())) {
            var militaryBuilding = new MilitaryBuilding();
            militaryBuilding
                    .setLevel(1L)
                    .setIsLock(true)
                    .setKosProfile(kosProfile)
                    .setName(BuildingName.MILITARY);
            militaryBuildingRepository.save(militaryBuilding);
        }
    }

    private void initDefaultValue(KosProfile kosProfile) {
        if (Objects.isNull(kosProfile.getReduceUpgradingTimePercent())) {
            kosProfile.setReduceUpgradingTimePercent(0.0);
        }
        if (Objects.isNull(kosProfile.getIsUnlockAdvancedMilitaryTech())) {
            kosProfile.setIsUnlockAdvancedMilitaryTech(false);
        }
        if (Objects.isNull(kosProfile.getBonusStoneProductionPercent())) {
            kosProfile.setBonusStoneProductionPercent(0.0);
        }
        if (Objects.isNull(kosProfile.getBonusWoodProductionPercent())) {
            kosProfile.setBonusWoodProductionPercent(0.0);
        }
        if (Objects.isNull(kosProfile.getIsUnlockMilitaryTech())) {
            kosProfile.setIsUnlockMilitaryTech(false);
        }
        if (Objects.isNull(kosProfile.getBonusGoldProductionPercent())) {
            kosProfile.setBonusGoldProductionPercent(0.0);
        }
        if (Objects.isNull(kosProfile.getBonusCapGoldStoragePercent())) {
            kosProfile.setBonusCapGoldStoragePercent(0.0);
        }
        if (Objects.isNull(kosProfile.getBonusCapWoodStoragePercent())) {
            kosProfile.setBonusCapWoodStoragePercent(0.0);
        }
        if (Objects.isNull(kosProfile.getBonusCapStoneStoragePercent())) {
            kosProfile.setBonusCapStoneStoragePercent(0.0);
        }
        if (Objects.isNull(kosProfile.getBonusEffectRelicItemPercent())) {
            kosProfile.setBonusEffectRelicItemPercent(0.0);
        }
        if (Objects.isNull(kosProfile.getCanUseSpeedItem())) {
            kosProfile.setCanUseSpeedItem(false);
        }
        if (Objects.isNull(kosProfile.getBonusProtectResourcePercent())) {
            kosProfile.setBonusProtectResourcePercent(0.0);
        }
    }

    /**
     * Vault building will be locked
     */
    private void initVaultBuilding(KosProfile kosProfile) {
        if (Objects.isNull(kosProfile.getId()) || !vaultBuildingRepository.existsByKosProfileId(kosProfile.getId())) {
            var vaultBuilding = new VaultBuilding();
            vaultBuilding.setLevel(1L)
                         .setIsLock(true)
                         .setKosProfile(kosProfile)
                         .setName(BuildingName.VAULT);
            buildingRepository.save(vaultBuilding);
        }
    }

    /**
     * Queen building will be locked
     */
    private void initQueenBuilding(KosProfile kosProfile) {
        if (Objects.isNull(kosProfile.getId()) || !queenBuildingRepository.existsByKosProfileId(kosProfile.getId())) {
            var queenBuilding = new QueenBuilding();
            queenBuilding
                    .setNumberOfQueenCard(0L)
                    .setLevel(1L)
                    .setIsLock(true)
                    .setKosProfile(kosProfile)
                    .setName(BuildingName.QUEEN)
            ;
            buildingRepository.save(queenBuilding);
        }
    }

    /**
     * Merge weapon feature will be locked
     */
    private void initArmoryBuilding(KosProfile kosProfile) {
        if (Objects.isNull(kosProfile.getId()) || !armoryBuildingRepository.existsByKosProfileId(kosProfile.getId())) {
            var armoryBuilding = new ArmoryBuilding();
            armoryBuilding.setIsLockMergeWeapon(true)
                          .setLevel(1L)
                          .setIsLock(true)
                          .setKosProfile(kosProfile)
                          .setName(BuildingName.ARMORY);
            armoryBuildingRepository.save(armoryBuilding);
        }
    }

    private void initScoutBuilding(KosProfile kosProfile) {
        if (Objects.isNull(kosProfile.getId()) || !scoutBuildingRepository.existsByKosProfile_Id(kosProfile.getId())) {
            var scoutBuilding = new ScoutBuilding();
            scoutBuilding.setUnlockScoutFeature(false)
                         .setTotalScout(0L)
                         .setAvailableScout(0L)
                         .setScoutTraining(0L)
                         .setLevel(1L)
                         .setIsLock(true)
                         .setKosProfile(kosProfile)
                         .setName(BuildingName.SCOUT);
            scoutBuildingRepository.save(scoutBuilding);
        }
    }

    private void initLighthouseBuilding(KosProfile kosProfile) {
        if (Objects.isNull(kosProfile.getId()) || lightHouseBuildingRepository.findByKosProfileId(kosProfile.getId()).isEmpty()) {
            var lightHouse = new LighthouseBuilding();
            lightHouse
                    .setLevel(1L)
                    .setIsLock(false)
                    .setKosProfile(kosProfile)
                    .setName(BuildingName.LIGHTHOUSE);
            buildingRepository.save(lightHouse);
        }
    }

    /**
     * Init Research Building
     */
    private void initResearchBuilding(KosProfile kosProfile) {
        if (Objects.isNull(kosProfile.getId()) || !researchBuildingRepository.existsByKosProfileId(kosProfile.getId())) {
            var userTechnologies = new ArrayList<UserTechnology>();
            for (TechnologyType value : TechnologyType.values()) {
                var tree = technologyRepository.findByTechnologyType(value);
                userTechnologies.addAll(tree.stream()
                                            .map(technology -> new UserTechnology().setTechnology(technology)).collect(Collectors.toList()));
            }

            var research = new ResearchBuilding();
            research.setLevel(1L)
                    .setKosProfile(kosProfile)
                    .setName(BuildingName.RESEARCH);
            userTechnologies.forEach(userTechnology -> {
                userTechnology.setResearchBuilding(research);
            });
            research.setUserTechnologies(userTechnologies);
            buildingRepository.save(research);
        }
    }

    /**
     * Stone mine will be locked
     */
    private void initStoneMine(KosProfile kosProfile) {
        if (Objects.isNull(kosProfile.getId()) || !stoneMineBuildingRepository.existsByKosProfileId(kosProfile.getId())) {

            var stoneMine = new StoneMineBuilding();
            stoneMine.setWorker(0L)
                     .setLastTimeClaim(LocalDateTime.now())
                     .setIsLock(true)
                     .setLevel(1L)
                     .setKosProfile(kosProfile)
                     .setName(BuildingName.STONE_MINE);
            buildingRepository.save(stoneMine);
        }
    }

    /**
     * Command port building will be locked
     */
    private void initCommandPortBuilding(KosProfile kosProfile) {
        if (Objects.isNull(kosProfile.getId()) || !commandBuildingRepository.existsByKosProfileId(kosProfile.getId())) {
            var commandBuilding = new CommandBuilding();
            commandBuilding
                    .setMaxSlotWeaponOfMotherShip(0L)
                    .setLevel(1L)
                    .setIsLock(true)
                    .setKosProfile(kosProfile)
                    .setName(BuildingName.COMMAND);
            commandBuildingRepository.save(commandBuilding);
        }
    }

    /**
     * Wood mine will be locked
     */
    private void initWoodMine(KosProfile kosProfile) {
        if (Objects.isNull(kosProfile.getId()) || !woodMineBuildingRepository.existsByKosProfileId(kosProfile.getId())) {

            var woodMine = new WoodMineBuilding();
            woodMine.setWorker(0L)
                    .setLastTimeClaim(LocalDateTime.now())
                    .setLevel(1L)
                    .setIsLock(true)
                    .setKosProfile(kosProfile)
                    .setName(BuildingName.WOOD_MINE);
            buildingRepository.save(woodMine);
        }
    }

    /**
     * Init 3 storage: stone, wood, gold
     * It will be lock util research its technology
     */
    private void initStorageBuilding(KosProfile kosProfile) {
        if (Objects.isNull(kosProfile.getId()) || !storageBuildingRepository.existsByKosProfileIdAndStorageType(kosProfile.getId(),
                                                                                                                StorageType.WOOD)) {

            var woodStorage = new StorageBuilding();
            woodStorage.setStorageType(StorageType.WOOD)
                       .setLevel(1L)
                       .setIsLock(true)
                       .setKosProfile(kosProfile)
                       .setName(BuildingName.STORAGE_WOOD);
            buildingRepository.save(woodStorage);
        }
        if (Objects.isNull(kosProfile.getId()) || !storageBuildingRepository.existsByKosProfileIdAndStorageType(kosProfile.getId(),
                                                                                                                StorageType.STONE)) {

            var stoneStorage = new StorageBuilding();
            stoneStorage.setStorageType(StorageType.STONE)
                        .setIsLock(true)
                        .setLevel(1L)
                        .setKosProfile(kosProfile)
                        .setName(BuildingName.STORAGE_STONE);
            buildingRepository.save(stoneStorage);
        }
        if (Objects.isNull(kosProfile.getId()) || !storageBuildingRepository.existsByKosProfileIdAndStorageType(kosProfile.getId(),
                                                                                                                StorageType.GOLD)) {

            var goldStorage = new StorageBuilding();
            goldStorage.setStorageType(StorageType.GOLD)
                       .setLevel(1L)
                       .setIsLock(true)
                       .setKosProfile(kosProfile)
                       .setName(BuildingName.STORAGE_GOLD);
            buildingRepository.save(goldStorage);
        }
    }

    public void initBaseUser(KosProfile kosProfile) {
        if (!userBaseService.existUserBase(kosProfile.getId())) {
            UserBase userBase = userBaseService.getUserBaseFree();
            UserProfile userProfile = userProfileService.findByUserId(kosProfile.getUser().getId());
            userBase.setKosProfile(kosProfile)
                    .setIslandName(userProfile.getUsername())
                    .setActive(true);
            mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(userBase));
        }
    }

    public InitAssetKosConfig getInitAssetConfig() {
        Config config = configProvider.getConfig(ConfigKey.INIT_ASSET_KOS);
        var initAssetKosConfig = gson.fromJson(config.getValue(), InitAssetKosConfig.class);
        return initAssetKosConfig;
    }

    public KosProfile getByAssetsId(Long assetsId) {
        return kosProfileRepository.findByAssetsId(assetsId).orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND));
    }

}

