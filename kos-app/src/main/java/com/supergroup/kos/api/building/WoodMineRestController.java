package com.supergroup.kos.api.building;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.kos.building.domain.command.GetAllUpgradeInfoCommand;
import com.supergroup.kos.building.domain.command.GetBuildingInfoCommand;
import com.supergroup.kos.building.domain.command.GetUpgradeInfoCommand;
import com.supergroup.kos.building.domain.command.GetUpgradeStatusCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.UpgradeBuildingCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.WoodMineConfig;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.WoodMineService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.upgrade.UpgradeService;
import com.supergroup.kos.dto.building.MineInfoResponse;
import com.supergroup.kos.dto.building.StupidWoodMineUpgradeInfoResponse;
import com.supergroup.kos.dto.building.UpdateWorkerRequest;
import com.supergroup.kos.dto.upgrade.UpgradeStatusResponse;
import com.supergroup.kos.mapper.UpgradeSessionMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/building/wood")
@RequiredArgsConstructor
public class WoodMineRestController {

    private final KosProfileService        kosProfileService;
    private final WoodMineService          woodMineService;
    private final BuildingConfigDataSource buildingConfigDataSource;
    private final UpgradeService           upgradeService;
    private final AssetsService            assetsService;
    private final UpgradeSessionMapper     upgradeSessionMapper;

    @GetMapping
    public ResponseEntity<?> getInfo() {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        var woodMine = woodMineService.getBuildingInfo(new GetBuildingInfoCommand(kosProfileId));
        var config = (WoodMineConfig) buildingConfigDataSource.getConfig(BuildingName.WOOD_MINE, woodMine.getLevel());
        return ResponseEntity.ok(new MineInfoResponse().setLevel(woodMine.getLevel())
                                                       .setDescription(woodMine.getDescription()) // TODO this is hard code
                                                       .setNumWorker(woodMine.getWorker())
                                                       .setMaxWorker(config.getMaxWorker())
                                                       .setSpeedWorker(woodMine.getCurrentSpeedPerWorker())
                                                       .setStorage(0L));
    }

    @PutMapping("/worker")
    public ResponseEntity<?> updateWorker(@RequestBody UpdateWorkerRequest request) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        woodMineService.changeWorker(kosProfileId, request.getAmount());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/upgrade")
    public ResponseEntity<?> upgrade() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var woodMine = woodMineService.getBuildingInfo(new GetBuildingInfoCommand(kosProfile.getId()));
        var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
        upgradeService.upgrade(new UpgradeBuildingCommand(kosProfile, woodMine, asset));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/upgrade/status")
    public ResponseEntity<UpgradeStatusResponse> upgradeStatus() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        UpgradeSession upgradeSession = upgradeService.getUpgradeSession(new GetUpgradeStatusCommand().setKosProfileId(kosProfile.getId())
                                                                                                      .setBuildingName(BuildingName.WOOD_MINE));
        return ResponseEntity.ok(upgradeSessionMapper.toUpgradeStatusResponse(upgradeSession));
    }

    @GetMapping("/upgrade")
    public ResponseEntity<?> getUpgradeInfo(@RequestParam(value = "level", required = false) Long level) {
        // @formatter:off
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        if (Objects.isNull(level)) {
            var command = new GetAllUpgradeInfoCommand(BuildingName.WOOD_MINE, kosProfile.getId());
            var upgradeInfos = (List<WoodMineConfig>) woodMineService.getAllUpgradeInfo(command);
            var listInfoResponse = upgradeInfos.stream()
                    .map(upgradeInfo -> new StupidWoodMineUpgradeInfoResponse().setTimeUpgrade(upgradeInfo.getUpgradeDuration())
                            .setLevel(upgradeInfo.getLevel())
                            .setLevelResearchBuilding(upgradeInfo.getResearchLevelRequired())
                            .setCostStone(upgradeInfo.getStone())
                            .setCostWood(upgradeInfo.getWood())
                            .setCostGold(upgradeInfo.getGold())
                            .setGpGain(upgradeInfo.getGpPointReward())
                            .setSpeedWorker(upgradeInfo.getWoodPerWorker())
                            .setMaxWorker(upgradeInfo.getMaxWorker()))
                    .collect(
                            Collectors.toList());
            return ResponseEntity.ok(Map.of("details", listInfoResponse));
        } else {
            var upgradeInfo = (WoodMineConfig) woodMineService.getUpgradeInfo(new GetUpgradeInfoCommand(level, kosProfile.getId()));
            return ResponseEntity.ok(new StupidWoodMineUpgradeInfoResponse().setTimeUpgrade(upgradeInfo.getUpgradeDuration())
                    .setLevelResearchBuilding(upgradeInfo.getResearchLevelRequired())
                    .setCostStone(upgradeInfo.getStone())
                    .setCostWood(upgradeInfo.getWood())
                    .setGpGain(upgradeInfo.getGpPointReward())
                    .setCostGold(upgradeInfo.getGold())
                    .setSpeedWorker(upgradeInfo.getWoodPerWorker())
                    .setMaxWorker(upgradeInfo.getMaxWorker()));
        }
        // @formatter:on
    }
}
