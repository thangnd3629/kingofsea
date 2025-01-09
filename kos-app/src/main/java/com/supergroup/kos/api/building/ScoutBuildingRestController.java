package com.supergroup.kos.api.building;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.auth.domain.model.UserProfile;
import com.supergroup.auth.domain.service.UserProfileService;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetAllUpgradeInfoCommand;
import com.supergroup.kos.building.domain.command.GetScoutBuildingInfoCommand;
import com.supergroup.kos.building.domain.command.GetScoutCaseConfigCommand;
import com.supergroup.kos.building.domain.command.GetUpgradeInfoCommand;
import com.supergroup.kos.building.domain.command.GetUpgradeStatusCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.ScoutTrainingCommand;
import com.supergroup.kos.building.domain.command.UpgradeBuildingCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.building.CastleBuilding;
import com.supergroup.kos.building.domain.model.config.ScoutBuildingConfig;
import com.supergroup.kos.building.domain.model.mining.ScoutBuilding;
import com.supergroup.kos.building.domain.model.scout.Location;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.CastleBuildingService;
import com.supergroup.kos.building.domain.service.building.ScoutBuildingService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.scout.ScoutService;
import com.supergroup.kos.building.domain.service.seamap.CalculateDistanceElementsService;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;
import com.supergroup.kos.building.domain.service.upgrade.UpgradeService;
import com.supergroup.kos.dto.building.ScoutBuildingInfoResponse;
import com.supergroup.kos.dto.building.ScoutTrainingRequest;
import com.supergroup.kos.dto.building.ScoutTrainingStatus;
import com.supergroup.kos.dto.scout.InfoEnemyInScout;
import com.supergroup.kos.dto.upgrade.UpgradeStatusResponse;
import com.supergroup.kos.mapper.ScoutBuildingMapper;
import com.supergroup.kos.mapper.ScoutConfigMapper;
import com.supergroup.kos.mapper.UpgradeSessionMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/building/scout")
@RequiredArgsConstructor
public class ScoutBuildingRestController {
    private final ScoutBuildingService             scoutBuildingService;
    private final KosProfileService                kosProfileService;
    private final AssetsService                    assetsService;
    private final UpgradeService                   upgradeService;
    private final CastleBuildingService            castleBuildingService;
    private final ScoutBuildingMapper              scoutBuildingMapper;
    private final ScoutConfigMapper                scoutConfigMapper;
    private final UserProfileService               userProfileService;
    private final AssetService                     assetService;
    private final UpgradeSessionMapper             upgradeSessionMapper;
    private final UserBaseService                  userBaseService;
    private final CalculateDistanceElementsService calculateDistanceElementsService;
    private final ScoutService                     scoutService;

    @GetMapping("")
    public ResponseEntity<ScoutBuildingInfoResponse> getBuildingInfo() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        ScoutBuilding scoutBuilding = scoutBuildingService.getBuildingInfo(new GetScoutBuildingInfoCommand(kosProfile.getId()));

        return ResponseEntity.ok(scoutBuildingMapper.toDTO(scoutBuilding));
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getBuildingDetailLevel() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        List<ScoutBuildingConfig> list = (List<ScoutBuildingConfig>) scoutBuildingService.getAllUpgradeInfo(
                new GetAllUpgradeInfoCommand(BuildingName.SCOUT, kosProfile.getId()));
        return ResponseEntity.ok(scoutConfigMapper.toConfigDetails(list));
    }

    @GetMapping("/upgrade")
    public ResponseEntity<?> getUpgradeInfo(@RequestParam(value = "level", required = false) Long level) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        if (Objects.isNull(level)) {
            var command = new GetAllUpgradeInfoCommand(BuildingName.SCOUT, kosProfile.getId());
            List<ScoutBuildingConfig> list = (List<ScoutBuildingConfig>) scoutBuildingService.getAllUpgradeInfo(command);
            return ResponseEntity.ok(Map.of("details", scoutConfigMapper.toUpgradeResponses(list)));
        } else {
            ScoutBuildingConfig config = (ScoutBuildingConfig) scoutBuildingService.getUpgradeInfo(
                    new GetUpgradeInfoCommand(level, kosProfile.getId()));
            return ResponseEntity.ok(scoutConfigMapper.toUpgradeResponse(config));
        }
    }

    @PostMapping("/upgrade")
    public ResponseEntity<?> upgrade() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var scoutBuilding = scoutBuildingService.getBuildingInfo(new GetScoutBuildingInfoCommand(kosProfile.getId()));
        var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
        upgradeService.upgrade(new UpgradeBuildingCommand(kosProfile, scoutBuilding, asset));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/upgrade/status")
    public ResponseEntity<UpgradeStatusResponse> upgradeStatus() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        UpgradeSession upgradeSession = upgradeService.getUpgradeSession(new GetUpgradeStatusCommand().setKosProfileId(kosProfile.getId())
                                                                                                      .setBuildingName(BuildingName.SCOUT));
        return ResponseEntity.ok(upgradeSessionMapper.toUpgradeStatusResponse(upgradeSession));
    }

    @GetMapping("/training")
    public ResponseEntity<?> getTrainingInfo() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var scoutBuilding = scoutBuildingService.getBuildingInfo(new GetScoutBuildingInfoCommand(kosProfile.getId()));
        ScoutBuildingConfig config = (ScoutBuildingConfig) scoutBuildingService.getUpgradeInfo(
                new GetUpgradeInfoCommand(scoutBuilding.getLevel(), kosProfile.getId()));
        return ResponseEntity.ok(scoutConfigMapper.toScoutTrainingInfo(config));
    }

    @PostMapping("/training")
    public ResponseEntity<?> trainingScout(@RequestBody ScoutTrainingRequest request) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        scoutBuildingService.trainingSoldier(new ScoutTrainingCommand().setKosProfileId(kosProfile.getId()).setSoldier(request.getSoldier()));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/training/status")
    public ResponseEntity<?> trainingStatus() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var scoutBuilding = scoutBuildingService.getBuildingInfo(new GetScoutBuildingInfoCommand(kosProfile.getId()));
        if (Objects.nonNull(scoutBuilding.getIsTraining()) && scoutBuilding.getIsTraining()) {
            return ResponseEntity.ok(new ScoutTrainingStatus().setDuration(scoutBuilding.getTrainingDuration()).setCurrent(
                                                                      Duration.between(scoutBuilding.getStartTrainingTime(), LocalDateTime.now()).toMillis())
                                                              .setScoutStraining(scoutBuilding.getScoutTraining()));
        } else {
            throw KOSException.of(ErrorCode.BUILDING_SCOUT_NOT_IN_TRAINING);
        }
    }

    @GetMapping("/case-mission")
    public ResponseEntity<?> getScoutCaseByNumberEnemy(@RequestParam(value = "enemy", required = true) Long enemy) {
        return ResponseEntity.ok(scoutConfigMapper.toScoutCaseConfigs(
                scoutBuildingService.getScoutCaseConfigByNumberEnemy(new GetScoutCaseConfigCommand().setEnemy(enemy))));
    }

    @GetMapping("/info-enemy/{kosProfileEnemyId}")
    @Transactional
    public ResponseEntity<?> getInfoEnemy(@PathVariable Long kosProfileEnemyId) {
        var kosProfileEnemy = kosProfileService.findById(kosProfileEnemyId).orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND));
        CastleBuilding castleBuilding = castleBuildingService.getCastleBuilding(new KosProfileCommand().setKosProfileId(kosProfileEnemyId));
        UserProfile userProfile = userProfileService.findByUserId(kosProfileEnemy.getUser().getId());
        ScoutBuilding scoutBuilding = scoutBuildingService.getBuildingInfo(
                new GetScoutBuildingInfoCommand(kosProfileEnemyId).setCheckUnlockBuilding(false));
        Double speed = scoutService.getSpeedScout();
        InfoEnemyInScout infoEnemyInScout = new InfoEnemyInScout();
        infoEnemyInScout.setName(userProfile.getUsername())
                        .setAvatarUrl(assetService.getUrl(userProfile.getAvatarUrl()))
                        .setLevel(castleBuilding.getLevel())
                        .setSpeed(speed);

        /**
         * TODO WARNING: THIS IS BAD CODE :( OPTIMIZE LATER
         * */
        // use blind scout ===================================
//        var enemy = Double.valueOf(0);
//        if (scoutBuilding.isBlindScout()) {
//            enemy = scoutBuilding.getAvailableScout().doubleValue() * scoutBuilding.blindMulti();
//            // disable effect item
//            scoutBuilding.setIsBlindScout(false);
//            scoutBuildingService.save(scoutBuilding);
//        } else {
//            enemy = scoutBuilding.getAvailableScout().doubleValue();
//        }
//        infoEnemyInScout.setEnemy(enemy.longValue());
        // ====================================================

        var enemy = scoutBuilding.getAvailableScout();
        infoEnemyInScout.setEnemy(enemy);

        UserBase seaBase = userBaseService.getByKosProfileId(kosProfileEnemy.getId());
        infoEnemyInScout.setLocation(new Location().setX(seaBase.getCoordinates().getX())
                                                   .setY(seaBase.getCoordinates().getY()));

        return ResponseEntity.ok(infoEnemyInScout);
    }
}
