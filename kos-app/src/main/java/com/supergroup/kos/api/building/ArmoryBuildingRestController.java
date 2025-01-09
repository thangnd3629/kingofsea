package com.supergroup.kos.api.building;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Iterables;
import com.supergroup.kos.building.domain.command.GetAllUpgradeInfoCommand;
import com.supergroup.kos.building.domain.command.GetArmoryBuildingInfoCommand;
import com.supergroup.kos.building.domain.command.GetUpgradeInfoCommand;
import com.supergroup.kos.building.domain.command.GetUpgradeStatusCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.UpgradeBuildingCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.ArmoryBuildingConfig;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.ArmoryBuildingService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.ship.EscortShipGroupLevelConfigService;
import com.supergroup.kos.building.domain.service.ship.EscortShipGroupService;
import com.supergroup.kos.building.domain.service.upgrade.UpgradeService;
import com.supergroup.kos.building.domain.service.weapon.WeaponSetLevelConfigService;
import com.supergroup.kos.dto.building.ArmoryBuildingResponse;
import com.supergroup.kos.dto.upgrade.UpgradeStatusResponse;
import com.supergroup.kos.mapper.ArmoryBuildingMapper;
import com.supergroup.kos.mapper.UpgradeArmoryBuildingInfoMapper;
import com.supergroup.kos.mapper.UpgradeSessionMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/building/armory")
@RequiredArgsConstructor
public class ArmoryBuildingRestController {

    private final ArmoryBuildingService             armoryBuildingService;
    private final WeaponSetLevelConfigService       weaponSetLevelConfigService;
    private final EscortShipGroupLevelConfigService escortShipGroupLevelConfigService;
    private final KosProfileService                 kosProfileService;
    private final AssetsService                     assetsService;
    private final UpgradeService                    upgradeService;
    private final ArmoryBuildingMapper              armoryBuildingMapper;
    private final UpgradeArmoryBuildingInfoMapper   upgradeArmoryBuildingInfoMapper;
    private final EscortShipGroupService            escortShipGroupService;
    private final UpgradeSessionMapper              upgradeSessionMapper;

    @GetMapping("")
    public ResponseEntity<ArmoryBuildingResponse> getBuildingInfo() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var armoryBuilding = armoryBuildingService.getBuildingInfo(new GetArmoryBuildingInfoCommand(kosProfile.getId()));
        var escortShipGroups = escortShipGroupService.getEscortShipGroups(kosProfile.getId());
        return ResponseEntity.ok(armoryBuildingMapper.toDTO(armoryBuilding.setEscortShipGroups(escortShipGroups)));
    }

    @GetMapping("/upgrade")
    public ResponseEntity<?> getUpgradeInfo(@RequestParam(value = "level", required = false) Long level) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        if (Objects.isNull(level)) {
            var command = new GetAllUpgradeInfoCommand(BuildingName.ARMORY, kosProfile.getId());
            var armoryBuildingConfigs = (List<ArmoryBuildingConfig>) armoryBuildingService.getAllUpgradeInfo(command);
            var responses = armoryBuildingConfigs.stream().peek(config -> {
                var weaponSetLevelConfigs = weaponSetLevelConfigService.getByArmoryBuildingConfigId(config.getId());
                var escortShipGroupLevelConfigs = escortShipGroupLevelConfigService.getByArmoryBuildingConfigId(config.getId());
                if (!weaponSetLevelConfigs.isEmpty()) {
                    config.setUnLockWeaponSetLevel(Iterables.getLast(weaponSetLevelConfigs).getLevel());
                }
                if (!escortShipGroupLevelConfigs.isEmpty()) {
                    config.setUnLockEscortShipGroupLevel(Iterables.getLast(escortShipGroupLevelConfigs).getLevel());
                    config.setUnLockEscortShipGroupName(Iterables.getLast(escortShipGroupLevelConfigs).getEscortShipGroupConfig().getName());
                }
            }).collect(Collectors.toList());
            return ResponseEntity.ok(Map.of("details", upgradeArmoryBuildingInfoMapper.toDTOs(responses)));
        } else {
            var armoryBuildingConfig = (ArmoryBuildingConfig) armoryBuildingService.getUpgradeInfo(
                    new GetUpgradeInfoCommand(level, kosProfile.getId()));
            var weaponSetLevelConfigs = weaponSetLevelConfigService.getByArmoryBuildingConfigId(armoryBuildingConfig.getId());
            var escortShipGroupLevelConfigs = escortShipGroupLevelConfigService.getByArmoryBuildingConfigId(armoryBuildingConfig.getId());
            if (!weaponSetLevelConfigs.isEmpty()) {
                var config = Iterables.getLast(weaponSetLevelConfigs);
                armoryBuildingConfig.setUnLockWeaponSetLevel(config.getLevel());
            }
            if (!escortShipGroupLevelConfigs.isEmpty()) {
                var config = Iterables.getLast(escortShipGroupLevelConfigs);
                armoryBuildingConfig.setUnLockEscortShipGroupLevel(config.getLevel())
                                    .setUnLockEscortShipGroupName(config.getEscortShipGroupConfig().getName());
            }
            return ResponseEntity.ok(upgradeArmoryBuildingInfoMapper.toDTO(armoryBuildingConfig));
        }
    }

    @PostMapping("/upgrade")
    public ResponseEntity<?> upgrade() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var queenBuilding = armoryBuildingService.getBuildingInfo(new GetArmoryBuildingInfoCommand(kosProfile.getId()));
        var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
        upgradeService.upgrade(new UpgradeBuildingCommand(kosProfile, queenBuilding, asset));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/upgrade/status")
    public ResponseEntity<UpgradeStatusResponse> upgradeStatus() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        UpgradeSession upgradeSession = upgradeService.getUpgradeSession(new GetUpgradeStatusCommand().setKosProfileId(kosProfile.getId())
                                                                                                      .setBuildingName(BuildingName.ARMORY));
        return ResponseEntity.ok(upgradeSessionMapper.toUpgradeStatusResponse(upgradeSession));
    }

}
