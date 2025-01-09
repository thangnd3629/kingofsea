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
import com.supergroup.kos.building.domain.command.GetMilitaryBuildingInfo;
import com.supergroup.kos.building.domain.command.GetUpgradeInfoCommand;
import com.supergroup.kos.building.domain.command.GetUpgradeStatusCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.UpgradeBuildingCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.MilitaryBuildingConfig;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.MilitaryBuildingService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.ship.EscortShipConfigService;
import com.supergroup.kos.building.domain.service.ship.EscortShipGroupService;
import com.supergroup.kos.building.domain.service.ship.EscortShipService;
import com.supergroup.kos.building.domain.service.upgrade.UpgradeService;
import com.supergroup.kos.dto.building.MilitaryBuildingResponse;
import com.supergroup.kos.dto.ship.EscortShipNameResponse;
import com.supergroup.kos.dto.upgrade.UpgradeStatusResponse;
import com.supergroup.kos.mapper.EscortShipGroupMapper;
import com.supergroup.kos.mapper.EscortShipStatisticMapper;
import com.supergroup.kos.mapper.UpgradeMilitaryBuildingInfoMapper;
import com.supergroup.kos.mapper.UpgradeSessionMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/building/military")
@RequiredArgsConstructor
public class MilitaryBuildingRestController {
    private final KosProfileService                 kosProfileService;
    private final MilitaryBuildingService           militaryBuildingService;
    private final AssetsService                     assetsService;
    private final UpgradeService                    upgradeService;
    private final EscortShipConfigService           escortShipConfigService;
    private final EscortShipService                 escortShipService;
    private final EscortShipGroupService            escortShipGroupService;
    private final UpgradeMilitaryBuildingInfoMapper upgradeMilitaryBuildingInfoMapper;
    private final UpgradeSessionMapper              upgradeSessionMapper;
    private final EscortShipStatisticMapper         escortShipStatisticMapper;
    private final EscortShipGroupMapper             escortShipGroupMapper;
    private final BuildingConfigDataSource          buildingConfigDataSource;

    @GetMapping("")
    public ResponseEntity<MilitaryBuildingResponse> getBuildingInfo() {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        var escortShipGroups = escortShipGroupService.getEscortShipGroups(kosProfileId);
        var militaryBuilding = militaryBuildingService.getBuildingInfo(new GetMilitaryBuildingInfo(kosProfileId));
        var militaryBuildingConfig = (MilitaryBuildingConfig) buildingConfigDataSource.getConfig(BuildingName.MILITARY,
                                                                                                 militaryBuilding.getLevel());
        var response = new MilitaryBuildingResponse();
        response.setLevel(militaryBuilding.getLevel());
        var groupResponse = escortShipGroups.stream().map(group -> {
            var escortShipStatisticResponses = group.getEscortShips().stream().map(ship -> {
                var baseSpeed = ship.getEscortShipConfig().getBuildDuration() * ship.getPercentSpeedBuild();
                var speedBuild = escortShipService.roundingTime(Math.round(baseSpeed),
                                                                militaryBuildingConfig.getPercentDurationBuildShip());
                var escortShipStatisticResponse = escortShipStatisticMapper.toDTO(ship);
                escortShipStatisticResponse.setSpeedBuild(speedBuild);
                return escortShipStatisticResponse;
            }).collect(Collectors.toList());
            var escortShipGroupResponse = escortShipGroupMapper.toDTO(group);
            escortShipGroupResponse.setEscortShips(escortShipStatisticResponses);
            return escortShipGroupResponse;
        }).collect(Collectors.toList());
        response.setEscortShipGroups(groupResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/upgrade")
    public ResponseEntity<?> getUpgradeInfo(@RequestParam(value = "level", required = false) Long level) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        if (Objects.isNull(level)) {
            var command = new GetAllUpgradeInfoCommand(BuildingName.MILITARY, kosProfile.getId());
            var militaryBuildingConfigs = (List<MilitaryBuildingConfig>) militaryBuildingService.getAllUpgradeInfo(command);
            var responses = upgradeMilitaryBuildingInfoMapper.toDTOs(militaryBuildingConfigs).stream().map(response -> {
                var escortShipConfigs = escortShipConfigService.findByMilitaryLevelRequiredOrderByIdAsc(response.getLevel());
                var escortShipConfigResponses = escortShipConfigs.stream().map(config -> new EscortShipNameResponse()
                        .setName(config.getType().getEscortShipTypeName())
                        .setType(config.getType())).collect(Collectors.toList());
                response.getReward().setUnLockEscortShips(escortShipConfigResponses);
                return response;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(Map.of("details", responses));
        } else {
            var militaryBuildingConfig = (MilitaryBuildingConfig) militaryBuildingService.getUpgradeInfo(
                    new GetUpgradeInfoCommand(level, kosProfile.getId()));
            var escortShipConfigs = escortShipConfigService.findByMilitaryLevelRequiredOrderByIdAsc(level);
            var escortShipConfigResponses = escortShipConfigs.stream().map(config -> new EscortShipNameResponse()
                    .setName(config.getType().getEscortShipTypeName())
                    .setType(config.getType())).collect(Collectors.toList());
            var response = upgradeMilitaryBuildingInfoMapper.toDTO(militaryBuildingConfig);
            response.getReward().setUnLockEscortShips(escortShipConfigResponses);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/upgrade")
    public ResponseEntity<?> upgrade() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var militaryBuilding = militaryBuildingService.getBuildingInfo(new GetMilitaryBuildingInfo(kosProfile.getId()));
        var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
        upgradeService.upgrade(new UpgradeBuildingCommand(kosProfile, militaryBuilding, asset));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/upgrade/status")
    public ResponseEntity<UpgradeStatusResponse> upgradeStatus() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        UpgradeSession upgradeSession = upgradeService.getUpgradeSession(new GetUpgradeStatusCommand().setKosProfileId(kosProfile.getId())
                                                                                                      .setBuildingName(BuildingName.MILITARY));
        return ResponseEntity.ok(upgradeSessionMapper.toUpgradeStatusResponse(upgradeSession));
    }
}
