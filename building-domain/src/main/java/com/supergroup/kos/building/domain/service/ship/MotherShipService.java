package com.supergroup.kos.building.domain.service.ship;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supergroup.auth.domain.model.User;
import com.supergroup.core.constant.BaseStatus;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.ShipUpgradeAsyncTask;
import com.supergroup.kos.building.domain.command.BuyMotherShipSystemCommand;
import com.supergroup.kos.building.domain.command.EquipWeaponCommand;
import com.supergroup.kos.building.domain.command.EquipWeaponsCommand;
import com.supergroup.kos.building.domain.command.GetCommandBuildingInfo;
import com.supergroup.kos.building.domain.command.GetMotherShipCommand;
import com.supergroup.kos.building.domain.command.GetWeaponByIdCommand;
import com.supergroup.kos.building.domain.command.GetWeaponSetCommand;
import com.supergroup.kos.building.domain.command.GetWeaponsCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.UpgradeMotherShipCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.MotherShipQualityKey;
import com.supergroup.kos.building.domain.constant.TechnologyCode;
import com.supergroup.kos.building.domain.constant.UpgradeMotherShipType;
import com.supergroup.kos.building.domain.constant.WeaponStat;
import com.supergroup.kos.building.domain.constant.seamap.SeaActivityStatus;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;
import com.supergroup.kos.building.domain.model.building.CommandBuilding;
import com.supergroup.kos.building.domain.model.config.CommandBuildingConfig;
import com.supergroup.kos.building.domain.model.config.MotherShipConfig;
import com.supergroup.kos.building.domain.model.config.MotherShipConfigQualityConfig;
import com.supergroup.kos.building.domain.model.config.MotherShipLevelConfig;
import com.supergroup.kos.building.domain.model.config.MotherShipQualityConfig;
import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.model.weapon.BaseWeapon;
import com.supergroup.kos.building.domain.model.weapon.Weapon;
import com.supergroup.kos.building.domain.model.weapon.WeaponSet;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.ship.MotherShipConfigQualityConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.ship.MotherShipLevelConfigRepository;
import com.supergroup.kos.building.domain.repository.persistence.ship.MotherShipRepository;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.battle.UpdateBattleUnitService;
import com.supergroup.kos.building.domain.service.building.CommandBuildingService;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;
import com.supergroup.kos.building.domain.service.technology.UserTechnologyService;
import com.supergroup.kos.building.domain.service.upgrade.UpgradeSessionService;
import com.supergroup.kos.building.domain.service.weapon.WeaponService;
import com.supergroup.kos.building.domain.service.weapon.WeaponSetService;
import com.supergroup.kos.building.domain.task.UpgradeTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MotherShipService {

    private static final int THRESHOLD = 2000; // mili

    private final WeaponService                           weaponService;
    private final WeaponSetService                        weaponSetService;
    private final MotherShipConfigQualityConfigDataSource motherShipConfigQualityConfigDataSource;
    private final MotherShipRepository                    motherShipRepository;
    private final CommandBuildingService                  commandBuildingService;
    private final UpgradeSessionService                   upgradeSessionService;
    private final BuildingConfigDataSource                buildingConfigDataSource;
    private final AssetsService                           assetsService;
    private final ObjectMapper                            objectMapper;
    private final MotherShipLevelConfigService            motherShipLevelConfigService;
    private final MotherShipQualityConfigService          motherShipQualityConfigService;
    private final UserTechnologyService                   userTechnologyService;
    private final RabbitTemplate                          rabbitTemplate;
    private final ShipUpgradeAsyncTask                    shipUpgradeAsyncTask;
    private final MotherShipLevelConfigRepository         motherShipLevelConfigRepository;
    private final UserBaseService                         userBaseService;
    private final UpdateBattleUnitService                 updateBattleUnitService;

    public List<MotherShip> findByKosProfileId(Long id) {
        return motherShipRepository.findByKosProfileId(id);
    }

    public void saveAll(List<MotherShip> list) {
        motherShipRepository.saveAll(list);
    }

    public void save(MotherShip ship) {
        motherShipRepository.save(ship);
    }

    public List<MotherShip> findByIdIn(List<Long> ids) {
        return motherShipRepository.findByIdIn(ids);
    }

    public List<MotherShip> getMotherShips(GetMotherShipCommand command) {
        var kosProfileId = command.getKosProfileId();
        var commandBuilding = commandBuildingService.getBuildingInfo(new GetCommandBuildingInfo(kosProfileId));
        var maxSlotWeapon = commandBuilding.getMaxSlotWeaponOfMotherShip();
        var motherShips = motherShipRepository
                .findByKosProfileId(kosProfileId)
                .stream()
                .peek(motherShip -> {
                    var slotWeapon = motherShip.getMotherShipConfigQualityConfig()
                                               .getMotherShipQualityConfig()
                                               .getSlotWeapon();
                    if (slotWeapon > maxSlotWeapon) {
                        if (maxSlotWeapon < 3) {
                            var tech = userTechnologyService.findByKosProfileIdAndTechnologyCode(TechnologyCode.MT5, kosProfileId);
                            motherShip.setTechnologyRequiredUnlockSlotWeapon(tech);
                        } else {
                            var tech = userTechnologyService.findByKosProfileIdAndTechnologyCode(TechnologyCode.MT13, kosProfileId);
                            motherShip.setTechnologyRequiredUnlockSlotWeapon(tech);
                        }
                    }
                    motherShip.setMaxSlotWeaponOfMotherShip(maxSlotWeapon);
                }).collect(Collectors.toList());
        return motherShips;
    }

    public MotherShip getMotherShipById(GetMotherShipCommand command) {
        var kosProfileId = command.getKosProfileId();
        var motherShip = motherShipRepository.findByIdAndKosProfileId(command.getMotherShipId(), kosProfileId)
                                             .orElseThrow(() -> KOSException.of(ErrorCode.MOTHER_SHIP_IS_NOT_FOUND));
        var commandBuilding = commandBuildingService.getBuildingInfo(new GetCommandBuildingInfo(kosProfileId));
        var maxSlotWeapon = commandBuilding.getMaxSlotWeaponOfMotherShip();
        var slotWeapon =
                motherShip.getMotherShipConfigQualityConfig().getMotherShipQualityConfig().getSlotWeapon();
        if (slotWeapon > maxSlotWeapon) {
            if (maxSlotWeapon < 3) {
                var tech = userTechnologyService.findByKosProfileIdAndTechnologyCode(TechnologyCode.MT5, kosProfileId);
                motherShip.setTechnologyRequiredUnlockSlotWeapon(tech);
            } else {
                var tech = userTechnologyService.findByKosProfileIdAndTechnologyCode(TechnologyCode.MT13, kosProfileId);
                motherShip.setTechnologyRequiredUnlockSlotWeapon(tech);
            }
        }
        motherShip.setMaxSlotWeaponOfMotherShip(maxSlotWeapon);
        return motherShip;
    }

    /** Upgrade level */
    @Transactional
    public void upgradeLevelMotherShip(UpgradeMotherShipCommand command) {
        var kosProfileId = command.getKosProfileId();
        var motherShipId = command.getMotherShipId();
        log.info("Upgrade level {} Mother ship of profile {}", motherShipId, motherShipId);
        try {
            var motherShip = getMotherShipById(new GetMotherShipCommand().setMotherShipId(motherShipId).setKosProfileId(kosProfileId));
            if (isBusy(motherShip)) {
                throw KOSException.of(ErrorCode.MOTHER_SHIP_ALREADY_ON_MISSION);
            }
            var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfileId));
            var commandBuilding = commandBuildingService.getBuildingInfo(new GetCommandBuildingInfo(kosProfileId));
            var commandBuildingConfig = (CommandBuildingConfig) buildingConfigDataSource.getConfig(BuildingName.COMMAND,
                                                                                                   commandBuilding.getLevel());
            var level = motherShip.getMotherShipLevelConfig().getLevel();
//            var maxLevel = commandBuildingConfig.getMaxLevelMotherShip();
            if (Objects.nonNull(motherShip.getUpgradeLevel())) {
                throw KOSException.of(ErrorCode.MOTHER_SHIP_IS_IN_OTHER_PROCESS);
            }
//            if (level >= maxLevel) {
//                throw KOSException.of(ErrorCode.CAN_NOT_UPGRADE_BECAUSE_THE_MAX_ALLOWED_LEVEL);
//            }
            var configNextLevel = motherShipLevelConfigService.getConfigByLevel(level + 1);
            if (!checkResourceUpgradeLevelRequirement(configNextLevel, asset)) {
                throw KOSException.of(ErrorCode.DO_NOT_MEET_RESOURCE_REQUIREMENT);
            }
            takeAssetToUpgradeLevel(configNextLevel, asset);
            startUpgradeLevelProcess(kosProfileId, motherShip, configNextLevel);
        } catch (KOSException kosException) {
            if (kosException.getCode().equals(ErrorCode.CONFIG_NOT_FOUND)) {
                throw KOSException.of(ErrorCode.CAN_NOT_UPGRADE_BECAUSE_NOT_FOUND_NEXT_LEVEL);
            } else {throw kosException;}
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw KOSException.of(ErrorCode.SERVER_ERROR);
        }
    }

    private void startUpgradeLevelProcess(Long kosProfileId, MotherShip motherShip, MotherShipLevelConfig config) throws
                                                                                                                  JsonProcessingException {
        // give building into upgrade process
        UpgradeSession upgradeSession = upgradeSessionService.createUpgradeMotherShipSession(motherShip, UpgradeMotherShipType.LEVEL,
                                                                                             LocalDateTime.now(), config.getUpgradeDuration());
        motherShip.setUpgradeLevel(upgradeSession);
        // save building state
        motherShipRepository.save(motherShip);

        var task = new UpgradeTask().setUpgradeSessionId(upgradeSession.getId());
        log.info("Send to queue");
        sendUpgradeTaskToQueue(task, config.getUpgradeDuration() - THRESHOLD);
    }

    private Boolean checkResourceUpgradeLevelRequirement(MotherShipLevelConfig config, Assets assets) {
        return assets.getWood() >= config.getWood()
               && assets.getGold() >= config.getGold()
               && assets.getStone() >= config.getStone();
    }

    private void takeAssetToUpgradeLevel(MotherShipLevelConfig config, Assets assets) {
        assets.setGold(assets.getGold() - config.getGold());
        assets.setStone(assets.getStone() - config.getStone());
        assets.setWood(assets.getWood() - config.getWood());
        assetsService.save(assets);
    }

    private void sendUpgradeTaskToQueue(UpgradeTask task, Long delay) throws JsonProcessingException {
        var taskJson = objectMapper.writeValueAsString(task);
        var prop = new MessageProperties();
        prop.setHeader("x-delay", delay);
        var mess = MessageBuilder.withBody(taskJson.getBytes())
                                 .andProperties(prop)
                                 .build();
        rabbitTemplate.convertAndSend("upgrading-exchange", "upgrading", mess);
    }

    public void validLevelUpgrading(User user, MotherShip motherShip, MotherShipLevelConfig nextLevelConfig) {
        shipUpgradeAsyncTask.sendMotherShipUpgradeLevelNotification(user.getId(), motherShip.getMotherShipConfigQualityConfig().getMotherShipConfig(),
                                                                    motherShip.getMotherShipLevelConfig(), nextLevelConfig);
        completeLevelUpgrading(motherShip, nextLevelConfig);
        log.info("Send notification to user id {}", user.getId());
    }

    private void completeLevelUpgrading(MotherShip motherShip, MotherShipLevelConfig nextLevelConfig) {
        motherShip.setMotherShipLevelConfig(nextLevelConfig);
        Long upgradeSessionId = Objects.nonNull(motherShip.getUpgradeLevel()) ? motherShip.getUpgradeLevel().getId() : null;
        motherShip.setUpgradeLevel(null);
        setCurrentHpForNewOrUpgradedShip(motherShip);
        motherShipRepository.save(motherShip);
        if (Objects.nonNull(upgradeSessionId)) {
            upgradeSessionService.deleteById(upgradeSessionId);
        }

    }

    private Boolean isUpgradeLevelDone(MotherShip motherShip) {
        if (Objects.isNull(motherShip.getUpgradeLevel())) {
            return true;
        }
        UpgradeSession upgradeSession = motherShip.getUpgradeLevel();
        return Objects.nonNull(upgradeSession.getTimeStart()) && Objects.nonNull(upgradeSession.getDuration())
               && upgradeSession.getTimeStart().plus(upgradeSession.getDuration() - THRESHOLD, ChronoUnit.MILLIS).isBefore(
                LocalDateTime.now());
    }

    /** End Upgrade */

    /** Equip And Remove Weapon - WeaponSet */
    @Transactional
    public void equipWeapon(EquipWeaponCommand command) {
        var kosProfileId = command.getKosProfileId();
        var weaponId = command.getWeaponId();
        var motherShipId = command.getMotherShipId();
        var isEquipping = command.getIsEquipping();
        var isWeaponSet = command.getIsWeaponSet();
        var motherShip = getMotherShipById(new GetMotherShipCommand().setMotherShipId(motherShipId).setKosProfileId(kosProfileId));

        if (isBusy(motherShip)) {
            throw KOSException.of(ErrorCode.MOTHER_SHIP_ALREADY_ON_MISSION);
        }

        var weapon = isWeaponSet.equals(false)
                     ? weaponService.getWeaponById(new GetWeaponByIdCommand().setWeaponId(weaponId).setKosProfileId(kosProfileId))
                     : weaponSetService.getWeaponSetById(new GetWeaponSetCommand().setWeaponSetId(weaponId).setKosProfileId(kosProfileId));
        if (isEquipping.equals(true) && !checkSlotWeapon(motherShip)) {
            throw KOSException.of(ErrorCode.MOTHER_SHIP_IS_EQUIPPED_FULL_WEAPON);
        }
        // Nếu weapon đã được trang bị trên tàu
        if (weapon.getMotherShip() != null) {
            // nếu là trang bị trên tàu mẹ này
            if (weapon.getMotherShip().getId().equals(motherShipId)) {
                // nếu là remove
                if (isEquipping.equals(false)) {
                    weapon.setMotherShip(null);
                    updateWeapon(weapon);
                }
                // Nếu là equip
                else {
                    throw KOSException.of(ErrorCode.WEAPON_IS_EQUIPPING_ON_THIS_MOTHER_SHIP);
                }
            }
            // nếu là trang bị trên tàu mẹ khác
            else {
                throw KOSException.of(ErrorCode.WEAPON_IS_EQUIPPING_ON_OTHER_MOTHER_SHIP);
            }
        }
        // Nếu weapon chưa được trang bị trên tàu
        else {
            if (isEquipping.equals(false)) {
                throw KOSException.of(ErrorCode.WEAPON_IS_NOT_EQUIPPING_ON_THIS_MOTHER_SHIP);
            } else {
                weapon.setMotherShip(motherShip);
                updateWeapon(weapon);
            }
        }
    }

    private void updateWeapon(BaseWeapon weapon) {
        if (weapon instanceof Weapon) {
            weaponService.save((Weapon) weapon);
        }
        if (weapon instanceof WeaponSet) {
            weaponSetService.save((WeaponSet) weapon);
        }
    }

    private Boolean checkSlotWeapon(MotherShip motherShip) {
        var slotWeapon = motherShip.getMotherShipConfigQualityConfig().getMotherShipQualityConfig().getSlotWeapon();
        var maxSlotWeapon = motherShip.getCommandBuilding().getMaxSlotWeaponOfMotherShip();
        var slotUnlock = slotWeapon > maxSlotWeapon ? maxSlotWeapon : slotWeapon;
        var slotEquipped = motherShip.getWeapons().size() + motherShip.getWeaponSets().size();
        return slotEquipped < slotUnlock;
    }

    @Transactional
    public void equipWeapons(EquipWeaponsCommand command) {
        var kosProfileId = command.getKosProfileId();
        var motherShipId = command.getMotherShipId();
        var motherShip = getMotherShipById(new GetMotherShipCommand().setKosProfileId(kosProfileId).setMotherShipId(motherShipId));
        var emptyId = new ArrayList<Long>();
        var weaponIds = command.getWeaponIds() == null ? emptyId : command.getWeaponIds();
        var weaponSetIds = command.getWeaponSetIds() == null ? emptyId : command.getWeaponSetIds();
        checkAndSaveWeapons(weaponIds, motherShip, kosProfileId);
        checkAndSaveWeaponSets(weaponSetIds, motherShip, kosProfileId);
    }

    private void checkAndSaveWeapons(List<Long> weaponIds, MotherShip motherShip, Long kosProfileId) {
        var weaponCanEquips = weaponService.getWeapons(new GetWeaponsCommand().setKosProfileId(kosProfileId)).stream().filter(
                weapon -> weapon.getMotherShip() == null || weapon.getMotherShip().getId().equals(motherShip.getId())).collect(Collectors.toList());
        weaponIds.forEach(id -> {
            var w = weaponService.getWeaponById(new GetWeaponByIdCommand().setKosProfileId(kosProfileId).setWeaponId(id));
            if (weaponCanEquips.stream().map(Weapon::getId).collect(Collectors.toList()).contains(id)) {
                weaponCanEquips.removeIf(weapon -> weapon.getId().equals(id));
                weaponService.save((Weapon) w.setMotherShip(motherShip));
            } else {
                log.error("weapon {} not valid", id);
                throw KOSException.of(ErrorCode.WEAPON_IS_NOT_VALID);
            }
        });
    }

    private void checkAndSaveWeaponSets(List<Long> weaponSetIds, MotherShip motherShip, Long kosProfileId) {
        var weaponSetCanEquips = weaponSetService.getWeaponSets(new GetWeaponSetCommand().setKosProfileId(kosProfileId))
                                                 .stream().filter(
                        weaponSet -> weaponSet.getMotherShip() == null || weaponSet.getMotherShip().getId().equals(motherShip.getId())).collect(
                        Collectors.toList());
        weaponSetIds.forEach(id -> {
            var ws = weaponSetService.getWeaponSetById(new GetWeaponSetCommand().setKosProfileId(kosProfileId).setWeaponSetId(id));
            if (weaponSetCanEquips.stream().map(WeaponSet::getId).collect(Collectors.toList()).contains(id)) {
                weaponSetCanEquips.remove(ws);
                weaponSetService.save((WeaponSet) ws.setMotherShip(motherShip));
            } else {
                log.error("weapon set {} not valid", id);
                throw KOSException.of(ErrorCode.WEAPON_SET_IS_NOT_VALID);
            }
        });
    }
    /** End Equip And Remove Weapon - WeaponSet */

    /**
     * Buy mother ship from system store
     */
    @Transactional()
    public MotherShip buyMotherShipFromSystemStore(BuyMotherShipSystemCommand command) {
        var kosProfileId = command.getKosProfileId();
        var configId = command.getMotherShipModelId();
        var commandBuilding = commandBuildingService.getBuildingInfo(new GetCommandBuildingInfo(kosProfileId));
        if (Objects.isNull(commandBuilding.getIsLockBuyMother())
            || commandBuilding.getIsLockBuyMother().equals(true)) {
            throw KOSException.of(ErrorCode.NOT_TECHNOLOGY_ELIGIBLE_FOR_BUY);
        }
        if (!checkSlotMotherShip(commandBuilding)) {
            throw KOSException.of(ErrorCode.NOT_ENOUGH_MOTHER_SHIP_SLOT);
        }
        var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfileId));
        var motherShipModel = motherShipConfigQualityConfigDataSource.getById(configId);

        if (!motherShipModel.getStatus().equals(BaseStatus.ACTIVATED)) {
            throw KOSException.of(ErrorCode.CANNOT_BUY_THIS_MOTHER_SHIP);
        }

        if (!checkResourceBuyRequirement(motherShipModel, asset)) {
            throw KOSException.of(ErrorCode.DO_NOT_MEET_RESOURCE_REQUIREMENT);
        }

        var levelConfig = motherShipLevelConfigService.getConfigByLevel(1L);
        takeAssetToBuy(motherShipModel, asset);
        var newMotherShip = new MotherShip()
                .setMotherShipLevelConfig(levelConfig)
                .setCommandBuilding(commandBuilding)
                .setStatus(SeaActivityStatus.STANDBY)
                .setMotherShipConfigQualityConfig(motherShipModel);
        setCurrentHpForNewOrUpgradedShip(newMotherShip);
        newMotherShip = motherShipRepository.save(newMotherShip);

        // for battle
        BattleProfile battleProfile = userBaseService.fightingOnMyBase(command.getKosProfileId());
        if (Objects.nonNull(battleProfile)) {
            updateBattleUnitService.addMotherShipForBattleWhenBuy(battleProfile, newMotherShip.getId());
        }
        return newMotherShip;
    }

    private void setCurrentHpForNewOrUpgradedShip(MotherShip motherShip) {
        Long maxHp = getMaxHp(motherShip).longValue();
        motherShip.setCurrentHp(maxHp);
    }

    private Double getMaxHp(MotherShip motherShip) {
        double totalPower = 0D;
        Double statBoostByLevel = motherShip.getMotherShipLevelConfig().getPercentStat();
        Double statBoostByQuality = motherShip.getMotherShipConfigQualityConfig().getMotherShipQualityConfig().getPercentStat();
        MotherShipConfig config = motherShip.getMotherShipConfigQualityConfig().getMotherShipConfig();
        WeaponStat weaponStat;
        double motherShipPower = config.getHp();
        weaponStat = WeaponStat.HP;
        totalPower += motherShipPower * statBoostByLevel * statBoostByQuality;
        Collection<Weapon> weapons = motherShip.getWeapons();
        Collection<WeaponSet> weaponSets = motherShip.getWeaponSets();
        totalPower += getWeaponPower(weapons, weaponStat) + getWeaponSetPower(weaponSets, weaponStat);
        return totalPower;
    }

    private Double getWeaponPower(Collection<Weapon> weapons, WeaponStat type) {
        if (Objects.isNull(weapons)) {
            return 0D;
        }
        double power = 0D;
        for (Weapon weapon : weapons) {
            WeaponStat statType = weapon.getWeaponConfig().getStat_type();
            if (statType.equals(type)) {
                power += weapon.getWeaponConfig().getStat();
            }
        }
        return power;
    }

    private Double getWeaponSetPower(Collection<WeaponSet> weaponSets, WeaponStat type) {
        if (Objects.isNull(weaponSets)) {
            return 0D;
        }
        double power = 0L;
        for (WeaponSet weaponSet : weaponSets) {
            WeaponStat statType = weaponSet.getWeaponSetConfig().getStat_type();
            if (statType.equals(type)) {
                power += weaponSet.getWeaponSetConfig().getStat() * weaponSet.getWeaponSetLevelConfig().getPercentStat();
            }
        }
        return power;
    }

    private Boolean checkSlotMotherShip(CommandBuilding commandBuilding) {
        return commandBuilding.getMotherShips().size() < commandBuilding.getSlotMotherShip();
    }

    private Boolean checkResourceBuyRequirement(MotherShipConfigQualityConfig config, Assets assets) {
        return assets.getGold() >= config.getGold();
    }

    private void takeAssetToBuy(MotherShipConfigQualityConfig config, Assets assets) {
        assets.setGold(assets.getGold() - config.getGold());
        assetsService.save(assets);
    }
    /** END BUY MOTHER SHIP SYSTEM */

    /** UPGRADE QUALITY */
    @Transactional
    public void upgradeQualityMotherShip(UpgradeMotherShipCommand command) {
        var kosProfileId = command.getKosProfileId();
        var motherShipId = command.getMotherShipId();
        log.info("Upgrade quality {} Mother ship of profile {}", motherShipId, motherShipId);
        try {
            var motherShip = getMotherShipById(new GetMotherShipCommand().setMotherShipId(motherShipId).setKosProfileId(kosProfileId));
            if (isBusy(motherShip)) {
                throw KOSException.of(ErrorCode.MOTHER_SHIP_ALREADY_ON_MISSION);
            }
            var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfileId));
            var commandBuilding = commandBuildingService.getBuildingInfo(new GetCommandBuildingInfo(kosProfileId));
            var commandBuildingConfig = (CommandBuildingConfig) buildingConfigDataSource.getConfig(BuildingName.COMMAND,
                                                                                                   commandBuilding.getLevel());

            var quality = motherShip.getMotherShipConfigQualityConfig().getMotherShipQualityConfig().getQuality();

//            var maxQuality = commandBuildingConfig.getMaxQualityMotherShip();
            if (Objects.nonNull(motherShip.getUpgradeQuality())) {
                throw KOSException.of(ErrorCode.MOTHER_SHIP_IS_IN_OTHER_PROCESS);
            }
//            if (quality.getKey() >= maxQuality.getKey()) {
//                throw KOSException.of(ErrorCode.CAN_NOT_UPGRADE_BECAUSE_THE_MAX_ALLOWED_QUALITY);
//            }
            var configNextQuality = motherShipQualityConfigService.getConfigByQuality(MotherShipQualityKey.getNextQuality(quality));
            if (!checkResourceUpgradeQualityRequirement(configNextQuality, asset)) {
                throw KOSException.of(ErrorCode.DO_NOT_MEET_RESOURCE_REQUIREMENT);
            }
            takeAssetToUpgradeQuality(configNextQuality, asset);
            startUpgradeQualityProcess(kosProfileId, motherShip, configNextQuality);
        } catch (KOSException kosException) {
            if (kosException.getCode().equals(ErrorCode.CONFIG_NOT_FOUND)) {
                throw KOSException.of(ErrorCode.CAN_NOT_UPGRADE_BECAUSE_NOT_FOUND_NEXT_LEVEL);
            } else {throw kosException;}
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw KOSException.of(ErrorCode.SERVER_ERROR);
        }
    }

    private Boolean checkResourceUpgradeQualityRequirement(MotherShipQualityConfig config, Assets assets) {
        return assets.getWood() >= config.getWood()
               && assets.getGold() >= config.getGold()
               && assets.getStone() >= config.getStone();
    }

    private void takeAssetToUpgradeQuality(MotherShipQualityConfig config, Assets assets) {
        assets.setGold(assets.getGold() - config.getGold());
        assets.setStone(assets.getStone() - config.getStone());
        assets.setWood(assets.getWood() - config.getWood());
        assetsService.save(assets);
    }

    private void startUpgradeQualityProcess(Long kosProfileId, MotherShip motherShip, MotherShipQualityConfig config) throws
                                                                                                                      JsonProcessingException {
        // give building into upgrade process
        UpgradeSession upgradeSession = upgradeSessionService.createUpgradeMotherShipSession(motherShip, UpgradeMotherShipType.QUALITY,
                                                                                             LocalDateTime.now(), config.getUpgradeDuration());
        motherShip.setUpgradeQuality(upgradeSession);
        // save building state
        motherShipRepository.save(motherShip);

        var task = new UpgradeTask().setUpgradeSessionId(upgradeSession.getId());
        log.info("Send to queue");
        sendUpgradeTaskToQueue(task, config.getUpgradeDuration() - THRESHOLD);
    }

    public void validQualityUpgrading(User user, MotherShip motherShip, MotherShipQualityConfig nextQualityConfig) {
        shipUpgradeAsyncTask.sendMotherShipUpgradeQualityNotification(user.getId(), motherShip.getMotherShipConfigQualityConfig()
                                                                                              .getMotherShipConfig(),
                                                                      motherShip.getMotherShipConfigQualityConfig()
                                                                                .getMotherShipQualityConfig(), nextQualityConfig);
        completeQualityUpgrading(motherShip, nextQualityConfig);
    }

    private Boolean isUpgradeQualityDone(MotherShip motherShip) {
        if (Objects.isNull(motherShip.getUpgradeQuality())) {
            return true;
        }
        UpgradeSession upgradeSession = motherShip.getUpgradeQuality();
        return Objects.nonNull(upgradeSession.getTimeStart()) && Objects.nonNull(upgradeSession.getDuration())
               && upgradeSession.getTimeStart().plus(upgradeSession.getDuration() - THRESHOLD, ChronoUnit.MILLIS).isBefore(
                LocalDateTime.now());
    }

    private void completeQualityUpgrading(MotherShip motherShip, MotherShipQualityConfig nextQualityConfig) {
        var modelConfigId = motherShip.getMotherShipConfigQualityConfig().getMotherShipConfig().getId();
        var qualityConfigId = motherShipQualityConfigService.getConfigByQuality(nextQualityConfig.getQuality()).getId();
        var newModel = motherShipConfigQualityConfigDataSource.getByModelIdAndQualityId(modelConfigId, qualityConfigId);
        motherShip.setMotherShipConfigQualityConfig(newModel);
        Long upgradeSessionId = Objects.nonNull(motherShip.getUpgradeQuality()) ? motherShip.getUpgradeQuality().getId() : null;
        motherShip.setUpgradeQuality(null);
        setCurrentHpForNewOrUpgradedShip(motherShip);
        motherShipRepository.save(motherShip);
        if (Objects.nonNull(upgradeSessionId)) {
            upgradeSessionService.deleteById(upgradeSessionId);
        }
    }

    @Transactional
    public void completeUpgradeMotherShip(UpgradeSession upgradeSession) {
        upgradeSession.setIsDeleted(true);
        upgradeSessionService.save(upgradeSession);
        var infoInstanceModel = upgradeSession.getInfoInstanceModel();
        var kosProfile = upgradeSession.getKosProfile();
        var motherShip = getMotherShipById(
                new GetMotherShipCommand().setMotherShipId(infoInstanceModel.getInstanceId()).setKosProfileId(kosProfile.getId()));
        if (infoInstanceModel.getUpgradeMotherShipType().equals(UpgradeMotherShipType.LEVEL)) {
            var levelConfig = motherShipLevelConfigRepository.findByLevel(motherShip.getMotherShipLevelConfig().getLevel() + 1)
                                                             .orElseThrow(() -> KOSException.of(
                                                                     ErrorCode.MOTHER_SHIP_LEVEL_CONFIG_IS_NOT_FOUND));
            validLevelUpgrading(kosProfile.getUser(), motherShip, levelConfig);
        }

        if (infoInstanceModel.getUpgradeMotherShipType().equals(UpgradeMotherShipType.QUALITY)) {
            var qualityConfig = motherShipQualityConfigService.getConfigByQuality(
                    MotherShipQualityKey.getNextQuality(
                            motherShip.getMotherShipConfigQualityConfig().getMotherShipQualityConfig().getQuality()));
            validQualityUpgrading(kosProfile.getUser(), motherShip, qualityConfig);
        }
    }

    private Boolean isBusy(MotherShip motherShip) {
        return
                !motherShip.getStatus().equals(SeaActivityStatus.STANDBY);
    }
}


