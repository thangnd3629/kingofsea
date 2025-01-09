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

import com.supergroup.kos.building.domain.command.GetAllUpgradeInfoCommand;
import com.supergroup.kos.building.domain.command.GetCommandBuildingInfo;
import com.supergroup.kos.building.domain.command.GetUpgradeInfoCommand;
import com.supergroup.kos.building.domain.command.GetUpgradeStatusCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.UpgradeBuildingCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.CommandBuildingConfig;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.repository.persistence.ship.MotherShipLevelConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.ship.MotherShipQualityConfigDataSource;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.CommandBuildingService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.upgrade.UpgradeService;
import com.supergroup.kos.dto.building.CommandBuildingResponse;
import com.supergroup.kos.dto.upgrade.UpgradeStatusResponse;
import com.supergroup.kos.mapper.CommandBuildingMapper;
import com.supergroup.kos.mapper.UpgradeCommandBuildingInfoMapper;
import com.supergroup.kos.mapper.UpgradeSessionMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/building/command")
@RequiredArgsConstructor
public class CommandBuildingRestController {

    private final KosProfileService                 kosProfileService;
    private final CommandBuildingService            commandBuildingService;
    private final AssetsService                     assetsService;
    private final UpgradeService                    upgradeService;
    private final CommandBuildingMapper             commandBuildingMapper;
    private final UpgradeCommandBuildingInfoMapper  upgradeCommandBuildingInfoMapper;
    private final MotherShipQualityConfigDataSource motherShipQualityConfigDataSource;
    private final MotherShipLevelConfigDataSource   motherShipLevelConfigDataSource;
    private final UpgradeSessionMapper              upgradeSessionMapper;

    @GetMapping("")
    public ResponseEntity<CommandBuildingResponse> getBuildingInfo() {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        var commandBuilding = commandBuildingService.getBuildingInfo(new GetCommandBuildingInfo(kosProfileId));
        return ResponseEntity.ok(commandBuildingMapper.toDTO(commandBuilding));
    }

    @GetMapping("/upgrade")
    public ResponseEntity<?> getUpgradeInfo(@RequestParam(value = "level", required = false) Long level) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        if (Objects.isNull(level)) {
            var command = new GetAllUpgradeInfoCommand(BuildingName.COMMAND, kosProfile.getId());
            var commandBuildingConfigs = (List<CommandBuildingConfig>) commandBuildingService.getAllUpgradeInfo(command);
            commandBuildingConfigs.stream().peek(config -> {
                var motherShipLevelConfigs = motherShipLevelConfigDataSource.getByCommandBuildingConfigId(config.getId());
                if (motherShipLevelConfigs.size() > 0) {
                    config.setUnLockMotherShipLevel(motherShipLevelConfigs.get(0).getLevel());
                }
                var motherShipQualityConfigs = motherShipQualityConfigDataSource.getByCommandBuildingConfigId(config.getId());
                if (motherShipQualityConfigs.size() > 0) {
                    config.setUnLockMotherShipQuality(motherShipQualityConfigs.get(0).getQuality());
                }
            }).collect(Collectors.toList());
            return ResponseEntity.ok(Map.of("details", upgradeCommandBuildingInfoMapper.toDTOs(commandBuildingConfigs)));
        } else {
            var commandBuildingConfig = (CommandBuildingConfig) commandBuildingService.getUpgradeInfo(
                    new GetUpgradeInfoCommand(level, kosProfile.getId()));
            var motherShipLevelConfigs = motherShipLevelConfigDataSource.getByCommandBuildingConfigId(commandBuildingConfig.getId());
            if (motherShipLevelConfigs.size() > 0) {
                commandBuildingConfig.setUnLockMotherShipLevel(motherShipLevelConfigs.get(0).getLevel());
            }
            var motherShipQualityConfigs = motherShipQualityConfigDataSource.getByCommandBuildingConfigId(commandBuildingConfig.getId());
            if (motherShipQualityConfigs.size() > 0) {
                commandBuildingConfig.setUnLockMotherShipQuality(motherShipQualityConfigs.get(0).getQuality());
            }
            var response = upgradeCommandBuildingInfoMapper.toDTO(commandBuildingConfig);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/upgrade")
    public ResponseEntity<?> upgrade() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var commandBuilding = commandBuildingService.getBuildingInfo(new GetCommandBuildingInfo(kosProfile.getId()));
        var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
        upgradeService.upgrade(new UpgradeBuildingCommand(kosProfile, commandBuilding, asset));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/upgrade/status")
    public ResponseEntity<UpgradeStatusResponse> upgradeStatus() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        UpgradeSession upgradeSession = upgradeService.getUpgradeSession(new GetUpgradeStatusCommand().setKosProfileId(kosProfile.getId())
                                                                                                      .setBuildingName(BuildingName.COMMAND));
        return ResponseEntity.ok(upgradeSessionMapper.toUpgradeStatusResponse(upgradeSession));
    }
}
