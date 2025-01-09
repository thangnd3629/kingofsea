package com.supergroup.kos.api.ship;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.BuildEscortShipCommand;
import com.supergroup.kos.building.domain.command.GetEscortShipCommand;
import com.supergroup.kos.building.domain.command.GetMilitaryBuildingInfo;
import com.supergroup.kos.building.domain.command.QueueEscortShipCommand;
import com.supergroup.kos.building.domain.command.UpgradeEscortShipCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.model.config.EscortShipConfig;
import com.supergroup.kos.building.domain.model.config.MilitaryBuildingConfig;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipLevelConfigDataSource;
import com.supergroup.kos.building.domain.service.building.MilitaryBuildingService;
import com.supergroup.kos.building.domain.service.config.KosConfigService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.ship.EscortShipService;
import com.supergroup.kos.building.domain.service.technology.UserTechnologyService;
import com.supergroup.kos.dto.ship.BuildEscortShipRequest;
import com.supergroup.kos.dto.ship.BuildShipStatusResponse;
import com.supergroup.kos.dto.ship.QueueEscortShipRequest;
import com.supergroup.kos.dto.ship.UpgradeLeveEscortShipRequest;
import com.supergroup.kos.dto.upgrade.UpgradeStatusResponse;
import com.supergroup.kos.mapper.BuildEscortShipInfoMapper;
import com.supergroup.kos.mapper.EscortShipMapper;
import com.supergroup.kos.mapper.TechnologyRequirementMapper;
import com.supergroup.kos.mapper.UpgradeEscortShipInfoMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/escort-ship")
@RequiredArgsConstructor
public class EscortShipRestController {

    private final KosProfileService               kosProfileService;
    private final EscortShipService               escortShipService;
    private final UserTechnologyService           userTechnologyService;
    private final AssetService                    assetService;
    private final MilitaryBuildingService         militaryBuildingService;
    private final KosConfigService                kosConfigService;
    private final EscortShipLevelConfigDataSource escortShipLevelConfigDataSource;
    private final EscortShipConfigDataSource      escortShipConfigDataSource;
    private final BuildingConfigDataSource        buildingConfigDataSource;
    private final EscortShipMapper                escortShipMapper;
    private final UpgradeEscortShipInfoMapper     upgradeEscortShipInfoMapper;
    private final TechnologyRequirementMapper     technologyRequirementMapper;
    private final BuildEscortShipInfoMapper       buildEscortShipInfoMapper;

    // Get Escort Ship
    @GetMapping("")
    public ResponseEntity<?> getEscortShipByType(@RequestParam(value = "type", required = false) EscortShipType type) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        if (Objects.isNull(type)) {
            var escortShips = escortShipService.getEscortShips(kosProfileId);
            var responses = escortShips.stream().map(escortShip -> {
                var res = escortShipMapper.toDTO(escortShip);
                var thumbnail = assetService.getUrl(escortShip.getEscortShipConfig().getThumbnail());
                var levelConfig = escortShipLevelConfigDataSource.getByTypeAndLevel(escortShip.getEscortShipConfig().getType(),
                                                                                    escortShip.getLevel());
                res.setPercentStat(levelConfig.getPercentStat());
                res.getModel().setThumbnail(thumbnail);
                return res;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } else {
            var escortShip = escortShipService.getEscortShipByShipType(new GetEscortShipCommand()
                                                                               .setShipType(type)
                                                                               .setKosProfileId(kosProfileId));
            var escortShipLevelConfig = escortShipLevelConfigDataSource.getByTypeAndLevel(escortShip.getEscortShipConfig().getType(),
                                                                                          escortShip.getLevel());
            var thumbnail = assetService.getUrl(escortShip.getEscortShipConfig().getThumbnail());
            var response = escortShipMapper.toDTO(escortShip);
            response.getModel().setThumbnail(thumbnail);
            response.setPercentStat(escortShipLevelConfig.getPercentStat());
            return ResponseEntity.ok(response);
        }
    }

    // Upgrade level escort ship
    @GetMapping("/upgrade")
    public ResponseEntity<?> getUpgradeInfo(@RequestParam(value = "level", required = false) Long level,
                                            @RequestParam(value = "type") EscortShipType type) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        if (Objects.isNull(level)) {
            var escortShipLevelConfigs = escortShipLevelConfigDataSource.getByType(type);
            var responses = escortShipLevelConfigs.stream().map(config -> {
                var technologyCode = config.getTechnologyCodeRequirement();
                var res = upgradeEscortShipInfoMapper.toDTO(config);
                if (Objects.nonNull(technologyCode)) {
                    var userTechnology = userTechnologyService.findByKosProfileIdAndTechnologyCode(technologyCode, kosProfileId);
                    var technologyRequirement = technologyRequirementMapper.toDTO(userTechnology);
                    res.getRequirement().setTechnology(technologyRequirement);
                }
                return res;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } else {
            var config = escortShipLevelConfigDataSource.getByTypeAndLevel(type, level);
            var technologyCode = config.getTechnologyCodeRequirement();
            var response = upgradeEscortShipInfoMapper.toDTO(config);
            if (Objects.nonNull(technologyCode)) {
                var userTechnology = userTechnologyService.findByKosProfileIdAndTechnologyCode(technologyCode, kosProfileId);
                var technologyRequirement = technologyRequirementMapper.toDTO(userTechnology);
                response.getRequirement().setTechnology(technologyRequirement);
            }
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/upgrade")
    public ResponseEntity<?> upgradeLevelEscortShip(@RequestBody UpgradeLeveEscortShipRequest request) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        escortShipService.upgradeEscortShip(new UpgradeEscortShipCommand(kosProfileId, request.getType()));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/upgrade/status")
    public ResponseEntity<UpgradeStatusResponse> upgradeStatus(@RequestParam(value = "type") EscortShipType type) {
        var kosProfileId = kosProfileService.findByUserId(AuthUtil.getUserId())
                                            .orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND)).getId();
        var escortShip = escortShipService.getEscortShipByShipType(new GetEscortShipCommand().setShipType(type).setKosProfileId(kosProfileId));

        if (Objects.nonNull(escortShip.getUpgradeSession())) {
            UpgradeSession upgradeSession = escortShip.getUpgradeSession();
            return ResponseEntity.ok(new UpgradeStatusResponse().setUpgradeSessionId(upgradeSession.getId())
                                                                .setDuration(upgradeSession.getDuration())
                                                                .setCurrent(
                                                                        Duration.between(upgradeSession.getTimeStart(),
                                                                                         LocalDateTime.now())
                                                                                .toMillis()));
        } else {
            throw KOSException.of(ErrorCode.ESCORT_SHIP_IS_NOT_IN_UPGRADING);
        }
    }

    // Build Escort Ship
    @GetMapping("/build")
    public ResponseEntity<?> getBuildInfo(@RequestParam(value = "type", required = false) EscortShipType type,
                                          @RequestParam(value = "isNextLevelBuilding", required = false) Boolean isNextLevelBuilding) {
        // @formatter:off
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var kosProfileId = kosProfile.getId();
        var militaryBuilding = militaryBuildingService.getBuildingInfo(new GetMilitaryBuildingInfo(kosProfileId));
        MilitaryBuildingConfig militaryBuildingConfig;
        if (Objects.isNull(isNextLevelBuilding) || isNextLevelBuilding.equals(false)) {
            militaryBuildingConfig = (MilitaryBuildingConfig) buildingConfigDataSource.getConfig(BuildingName.MILITARY,
                                                                                                 militaryBuilding.getLevel());
        } else {
            militaryBuildingConfig = (MilitaryBuildingConfig) buildingConfigDataSource.getConfig(BuildingName.MILITARY,
                                                                                                 militaryBuilding.getLevel() + 1);
        }
        var percentBuildTime = militaryBuildingConfig.getPercentDurationBuildShip();
        if (Objects.isNull(type)) {
            var escortShipLevelConfigs = escortShipConfigDataSource.getAll()
                                                                   .stream()
                                                                   .peek(config -> {
                                                                       var command = new GetEscortShipCommand().setShipType(config.getType())
                                                                                                               .setKosProfileId(kosProfileId);
                                                                       var escortShip = escortShipService.getEscortShipByShipType(command);
                                                                       var buildDuration = Math.round(config.getBuildDuration() * escortShip.getPercentSpeedBuild());
                                                                       config.setBuildDuration(escortShipService.roundingTime(buildDuration, percentBuildTime))
                                                                             .setPercentRssBuild(escortShip.getPercentRssBuild());
                                                                   })
                                                                   .peek((config) -> {
                                                                       validateBuildEscortShipConfig(kosProfile, config);
                                                                   })
                                                                   .collect(Collectors.toList());
            return ResponseEntity.ok(buildEscortShipInfoMapper.toDTOs(escortShipLevelConfigs));
        } else {
            var escortShipConfig = escortShipConfigDataSource.getByType(type);
            validateBuildEscortShipConfig(kosProfile, escortShipConfig);
            var command = new GetEscortShipCommand().setShipType(type)
                                                    .setKosProfileId(kosProfileId);
            var escortShip = escortShipService.getEscortShipByShipType(command);
            var baseBuildDuration = Math.round(
                    escortShipConfig.getBuildDuration() * escortShip.getPercentSpeedBuild());
            var buildDuration = escortShipService.roundingTime(baseBuildDuration, percentBuildTime);
            escortShipConfig.setBuildDuration(buildDuration).setPercentRssBuild(escortShip.getPercentRssBuild());
            return ResponseEntity.ok(buildEscortShipInfoMapper.toDTO(escortShipConfig));
        }
        // @formatter:on
    }

    @PostMapping("/build")
    public ResponseEntity<?> buildEscortShip(@Valid @RequestBody BuildEscortShipRequest request) {
        var userId = AuthUtil.getUserId();
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(userId)).getId();
        escortShipService.buildEscortShip(new BuildEscortShipCommand()
                                                  .setUserId(userId)
                                                  .setKosProfileId(kosProfileId)
                                                  .setType(request.getType())
                                                  .setAmount(request.getAmount())
                                                  .setIsCharged(false));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/build/status")
    public ResponseEntity<List<BuildShipStatusResponse>> buildStatus() {
        var kosProfile = kosProfileService.findByUserId(AuthUtil.getUserId())
                                          .orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND));
        var escortShips = escortShipService.getEscortShipsBuildingOrQueueing(kosProfile.getId());
        var militaryBuilding = militaryBuildingService.getBuildingInfo(new GetMilitaryBuildingInfo(kosProfile.getId()));
        var militaryBuildingConfig = (MilitaryBuildingConfig) buildingConfigDataSource.getConfig(BuildingName.MILITARY,
                                                                                                 militaryBuilding.getLevel());
        if (escortShips.isEmpty()) {
            throw KOSException.of(ErrorCode.ESCORT_SHIP_IS_NOT_BUILDING);
        }
        var responses = escortShips.stream().map(escortShip -> {
            UpgradeSession buildSession = escortShip.getBuildSession();
            var amount = escortShip.getNumberOfShipBuilding();
            if (Objects.isNull(amount)) {
                throw KOSException.of(ErrorCode.AMOUNT_BUILD_SHIP_IS_ILLEGAL);
            }
            var isBuilding = !Objects.isNull(escortShip.getBuildSession());
            var response = new BuildShipStatusResponse()
                    .setIsBuilding(isBuilding)
                    .setGroupName(escortShip.getEscortShipGroup().getEscortShipGroupLevelConfig().getEscortShipGroupConfig().getName())
                    .setAmount(amount)
                    .setType(escortShip.getEscortShipConfig().getType());

            if (Objects.nonNull(buildSession)) {
                response.setCurrent(
                                Duration.between(buildSession.getTimeStart(),
                                                 LocalDateTime.now())
                                        .toMillis())
                        .setDuration(buildSession.getDuration())
                        .setBuildSessionId(buildSession.getId());
            } else {
                var duration = escortShipService.roundingTime(escortShip.getEscortShipConfig().getBuildDuration(),
                                                              militaryBuildingConfig.getPercentDurationBuildShip()) * amount;
                response.setDuration(duration);
            }
            return response;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/queue")
    public ResponseEntity<?> queueEscortShip(@Valid @RequestBody QueueEscortShipRequest request) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        escortShipService.queueBuildEscortShip(
                new QueueEscortShipCommand(kosProfileId, request.getType(), request.getAmount(), request.getIsQueueing()));
        return ResponseEntity.accepted().build();
    }

    private void validateBuildEscortShipConfig(KosProfile kosProfile, EscortShipConfig config) {
        // if base is occupied, increase cost build
        config.setGold(escortShipService.getGoldCostToBuild(kosProfile, config).longValue());
        config.setStone(escortShipService.getStoneCostToBuild(kosProfile, config).longValue());
        config.setWood(escortShipService.getWoodCostToBuild(kosProfile, config).longValue());
    }
}
