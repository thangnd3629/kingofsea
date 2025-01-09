package com.supergroup.kos.building.domain.service.weapon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.supergroup.core.constant.BaseStatus;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.core.utils.RandomUtil;
import com.supergroup.kos.building.domain.async.WeaponSetUpgradeAsyncTask;
import com.supergroup.kos.building.domain.command.CreateWeaponSetCommand;
import com.supergroup.kos.building.domain.command.GetArmoryBuildingInfoCommand;
import com.supergroup.kos.building.domain.command.GetWeaponSetCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.UpdateGoldCommand;
import com.supergroup.kos.building.domain.command.UpgradeWeaponSetCommand;
import com.supergroup.kos.building.domain.constant.TechnologyCode;
import com.supergroup.kos.building.domain.constant.WeaponSetLevel;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.config.WeaponConfig;
import com.supergroup.kos.building.domain.model.config.WeaponSetConfig;
import com.supergroup.kos.building.domain.model.config.WeaponSetLevelConfig;
import com.supergroup.kos.building.domain.model.weapon.BaseWeapon;
import com.supergroup.kos.building.domain.model.weapon.Weapon;
import com.supergroup.kos.building.domain.model.weapon.WeaponSet;
import com.supergroup.kos.building.domain.repository.persistence.asset.AssetsRepository;
import com.supergroup.kos.building.domain.repository.persistence.weapon.WeaponRepository;
import com.supergroup.kos.building.domain.repository.persistence.weapon.WeaponSetLevelConfigRepository;
import com.supergroup.kos.building.domain.repository.persistence.weapon.WeaponSetRepository;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.ArmoryBuildingService;
import com.supergroup.kos.building.domain.service.technology.UserTechnologyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeaponSetService {
    private final WeaponSetConfigService         weaponSetConfigService;
    private final AssetsService                  assetsService;
    private final ArmoryBuildingService          armoryBuildingService;
    private final WeaponSetLevelConfigService    weaponSetLevelConfigService;
    private final WeaponRepository               weaponRepository;
    private final WeaponSetLevelConfigRepository weaponSetLevelConfigRepository;
    private final AssetsRepository               assetsRepository;
    private final WeaponSetRepository            weaponSetRepository;
    private final UserTechnologyService          userTechnologyService;
    private final WeaponSetUpgradeAsyncTask      weaponSetUpgradeAsyncTask;

    public Page<WeaponSet> getWeaponSets(GetWeaponSetCommand command, Pageable pageable) {
        return weaponSetRepository.findByKosProfileId(command.getKosProfileId(), pageable);
    }

    public List<WeaponSet> getWeaponSets(GetWeaponSetCommand command) {
        return weaponSetRepository.findByKosProfileId(command.getKosProfileId());
    }

    public WeaponSet getWeaponSetById(GetWeaponSetCommand command) {
        return weaponSetRepository.findByKosProfileIdAndWeaponSetId(command.getKosProfileId(), command.getWeaponSetId())
                                  .orElseThrow(() -> KOSException.of(ErrorCode.WEAPON_SET_IS_NOT_FOUND));

    }

    @Transactional
    public BaseWeapon createWeaponSet(CreateWeaponSetCommand command) {
        var weaponSetConfigId = command.getWeaponSetModelId();
        var kosProfileId = command.getKosProfileId();
        var assets = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfileId));
        var weaponSetConfig = weaponSetConfigService.getWeaponSetConfigById(weaponSetConfigId);
        checkRequirement(weaponSetConfig, kosProfileId);
        assetsService.updateGold(new UpdateGoldCommand().setKosProfileId(kosProfileId).setDiffGold(weaponSetConfig.getGold() * -1));
        var weapons = new ArrayList<Weapon>();
        weaponSetConfig.getWeaponConfigs()
                       .stream()
                       .filter(w -> w.getStatus().equals(BaseStatus.ACTIVATED))
                       .forEach(wc -> {
                           var weapon = weaponRepository.findByWeaponConfigIdAndKosProfileId(wc.getId(), kosProfileId);
                           weapons.add(weapon.get(0));
                       });
        if (!RandomUtil.random((float) (weaponSetConfig.getPercentSuccess() + 0))) {
            var weaponLost = RandomUtil.random(weapons);
            weaponRepository.delete(weaponLost.getId());
            weaponSetUpgradeAsyncTask.sendWeaponCraftFailNotification(assets.getKosProfile().getUser().getId(), weaponSetConfig.getGold(), weapons,
                                                                      List.of(weaponLost), weaponSetConfig);
            return weaponLost;
        }
        var weaponSetLevelConfig = weaponSetLevelConfigRepository.findByLevel(WeaponSetLevel.COMMON)
                                                                 .orElseThrow(() -> KOSException.of(ErrorCode.WEAPON_SET_LEVEL_CONFIG_IS_NOT_FOUND));
        var weaponSet = new WeaponSet()
                .setAssets(assets)
                .setWeaponSetConfig(weaponSetConfig)
                .setWeaponSetLevelConfig(weaponSetLevelConfig);

        weapons.forEach(w -> weaponRepository.delete(w.getId()));
        weaponSetUpgradeAsyncTask.sendWeaponCraftSuccessNotification(assets.getKosProfile().getUser().getId(), weaponSetConfig.getGold(), weapons,
                                                                     weaponSet);
        return weaponSetRepository.save(weaponSet);
    }

    private void checkRequirement(WeaponSetConfig weaponSetConfig, Long kosProfileId) {
        if (!checkWeaponRequirement(weaponSetConfig.getWeaponConfigs()
                                                   .stream()
                                                   .filter(w -> w.getStatus().equals(BaseStatus.ACTIVATED))
                                                   .collect(Collectors.toList()),
                                    kosProfileId)) {
            throw KOSException.of(ErrorCode.NOT_ENOUGH_WEAPON);
        }
        if (!checkoutAssetRequirement(weaponSetConfig.getGold(), kosProfileId)) {
            throw KOSException.of(ErrorCode.DO_NOT_MEET_RESOURCE_REQUIREMENT);
        }
        if (!checkTechnologyRequirement(weaponSetConfig.getTechnologyRequirement(), kosProfileId)) {
            throw KOSException.of(ErrorCode.CAN_NOT_CRAFT_BECAUSE_THE_TECHNOLOGY_IS_NOT_RESEARCHED);
        }
    }

    private Boolean checkWeaponRequirement(List<WeaponConfig> weaponConfigs, Long kosProfileId) {
        var weapons = weaponRepository.findByKosProfileIdAndMotherShipNull(kosProfileId);
        return weaponConfigs.stream().allMatch(
                weaponConfig -> weapons.stream().anyMatch(weapon -> {
                    return weaponConfig.getId().equals(weapon.getWeaponConfig().getId());
                }));
    }

    private Boolean checkoutAssetRequirement(Long gold, Long kosProfileId) {
        var assets = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfileId));
        return assets.getGold() >= gold;
    }

    private Boolean checkTechnologyRequirement(TechnologyCode technologyCodeRequirement, Long kosProfileId) {
        if (Objects.isNull(technologyCodeRequirement)) {
            return true;
        }
        var ut = userTechnologyService.findByKosProfileIdAndTechnologyCode(technologyCodeRequirement, kosProfileId);
        return !Objects.isNull(ut.getIsResearched()) && ut.getIsResearched();
    }

    @Transactional
    public void upgrade(UpgradeWeaponSetCommand command) {

        var weaponSet = getWeaponSetById(
                new GetWeaponSetCommand().setKosProfileId(command.getKosProfileId()).setWeaponSetId(command.getWeaponSetId()));
        if (Objects.nonNull(weaponSet.getMotherShip())) {
            throw KOSException.of(ErrorCode.CAN_NOT_UPGRADE_BECAUSE_WEAPON_SET_IS_EQUIPPING_ON_MOTHER_SHIP);
        }
        var weaponSetLevel = weaponSet.getWeaponSetLevelConfig().getLevel();
        var assets = assetsService.getAssets(new KosProfileCommand().setKosProfileId(command.getKosProfileId()));
        var armoryBuilding = armoryBuildingService.getBuildingInfo(new GetArmoryBuildingInfoCommand(command.getKosProfileId()));
        if (weaponSetLevel.equals(WeaponSetLevel.LEGENDARY)) {
            throw KOSException.of(ErrorCode.CAN_NOT_UPGRADE_BECAUSE_THE_LEVEL_IS_MAXED);
        }
        var weaponSetNextLevel = WeaponSetLevel.getNextLevel(weaponSetLevel);
        var weaponSetLevelConfig = weaponSetLevelConfigService.getWeaponSetLevelConfigByLevel(weaponSetLevel);
        var weaponSetNextLevelConfig = weaponSetLevelConfigService.getWeaponSetLevelConfigByLevel(weaponSetNextLevel);
        if (!checkResourceRequirement(weaponSetNextLevelConfig, assets)) {
            throw KOSException.of(ErrorCode.DO_NOT_MEET_RESOURCE_REQUIREMENT);
        }
        if (weaponSetNextLevelConfig.getArmoryBuildingConfig().getLevel() > armoryBuilding.getLevel()) {
            throw KOSException.of(ErrorCode.ARMORY_BUILDING_IS_NOT_LEVEL_ENOUGH);
        }
        weaponSet.setWeaponSetLevelConfig(weaponSetNextLevelConfig);
        takeAssetToUpgrade(weaponSetNextLevelConfig, assets);
        weaponSetUpgradeAsyncTask.sendUpgradeQualityNotification(assets.getKosProfile().getUser().getId(), weaponSetLevelConfig,
                                                                 weaponSetNextLevelConfig,
                                                                 weaponSet.getWeaponSetConfig());
        weaponSetRepository.save(weaponSet);
    }

    private Boolean checkResourceRequirement(WeaponSetLevelConfig weaponSetNextLevelConfig, Assets assets) {
        return assets.getWood() >= weaponSetNextLevelConfig.getWood()
               && assets.getGold() >= weaponSetNextLevelConfig.getGold()
               && assets.getStone() >= weaponSetNextLevelConfig.getStone();
    }

    private void takeAssetToUpgrade(WeaponSetLevelConfig weaponSetLevelConfig, Assets assets) {
        assets.setGold(assets.getGold() - weaponSetLevelConfig.getGold());
        assets.setStone(assets.getStone() - weaponSetLevelConfig.getStone());
        assets.setWood(assets.getWood() - weaponSetLevelConfig.getWood());
        assetsRepository.save(assets);
    }

    public WeaponSet save(WeaponSet weaponSet) {
        return weaponSetRepository.save(weaponSet);
    }
}
