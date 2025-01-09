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
import com.supergroup.kos.building.domain.model.config.StoneMineConfig;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.StoneMineService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.upgrade.UpgradeService;
import com.supergroup.kos.dto.building.MineInfoResponse;
import com.supergroup.kos.dto.building.StupidStoneMineUpgradeInfoResponse;
import com.supergroup.kos.dto.building.UpdateWorkerRequest;
import com.supergroup.kos.dto.upgrade.UpgradeStatusResponse;
import com.supergroup.kos.mapper.UpgradeSessionMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/building/stone")
@RequiredArgsConstructor
public class StoneMineRestController {

    private final KosProfileService        kosProfileService;
    private final StoneMineService         stoneMineService;
    private final BuildingConfigDataSource buildingConfigDataSource;
    private final UpgradeService           upgradeService;
    private final AssetsService            assetsService;
    private final UpgradeSessionMapper     upgradeSessionMapper;

    @GetMapping
    public ResponseEntity<?> getInfo() {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        var stoneMine = stoneMineService.getBuildingInfo(new GetBuildingInfoCommand(kosProfileId));
        var config = (StoneMineConfig) buildingConfigDataSource.getConfig(BuildingName.STONE_MINE, stoneMine.getLevel());
        return ResponseEntity.ok(new MineInfoResponse().setLevel(stoneMine.getLevel())
                                                       .setDescription(stoneMine.getDescription()) // TODO this is hard code
                                                       .setNumWorker(stoneMine.getWorker())
                                                       .setMaxWorker(config.getMaxWorker())
                                                       .setSpeedWorker(stoneMine.getCurrentSpeedPerWorker())
                                                       .setStorage(0L));
    }

    @PutMapping("/worker")
    public ResponseEntity<?> updateWorker(@RequestBody UpdateWorkerRequest request) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        stoneMineService.changeWorker(kosProfileId, request.getAmount());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/upgrade")
    public ResponseEntity<?> upgrade() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var stoneMine = stoneMineService.getBuildingInfo(new GetBuildingInfoCommand(kosProfile.getId()));
        var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
        upgradeService.upgrade(new UpgradeBuildingCommand(kosProfile, stoneMine, asset));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/upgrade/status")
    public ResponseEntity<UpgradeStatusResponse> upgradeStatus() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        UpgradeSession upgradeSession = upgradeService.getUpgradeSession(new GetUpgradeStatusCommand().setKosProfileId(kosProfile.getId())
                                                                                                      .setBuildingName(BuildingName.STONE_MINE));
        return ResponseEntity.ok(upgradeSessionMapper.toUpgradeStatusResponse(upgradeSession));
    }

    @GetMapping("/upgrade")
    public ResponseEntity<?> getUpgradeInfo(@RequestParam(value = "level", required = false) Long level) {
        // @formatter:off
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        if (Objects.isNull(level)) {
            var command = new GetAllUpgradeInfoCommand(BuildingName.STONE_MINE, kosProfile.getId());
            var upgradeInfos = (List<StoneMineConfig>) stoneMineService.getAllUpgradeInfo(command);
            var listInfoResponse = upgradeInfos.stream()
                    .map(upgradeInfo -> new StupidStoneMineUpgradeInfoResponse().setTimeUpgrade(upgradeInfo.getUpgradeDuration())
                            .setLevel(upgradeInfo.getLevel())
                            .setLevelResearchBuilding(upgradeInfo.getResearchLevelRequired())
                            .setCostStone(upgradeInfo.getStone())
                            .setCostWood(upgradeInfo.getWood())
                            .setCostGold(upgradeInfo.getGold())
                            .setGpGain(upgradeInfo.getGpPointReward())
                            .setSpeedWorker(upgradeInfo.getStonePerWorker().longValue())
                            .setMaxWorker(upgradeInfo.getMaxWorker()))
                    .collect(
                            Collectors.toList());
            return ResponseEntity.ok(Map.of("details", listInfoResponse));
        } else {
            var upgradeInfo = (StoneMineConfig) stoneMineService.getUpgradeInfo(new GetUpgradeInfoCommand(level, kosProfile.getId()));
            return ResponseEntity.ok(new StupidStoneMineUpgradeInfoResponse().setTimeUpgrade(upgradeInfo.getUpgradeDuration())
                    .setLevelResearchBuilding(upgradeInfo.getResearchLevelRequired())
                    .setCostStone(upgradeInfo.getStone())
                    .setCostWood(upgradeInfo.getWood())
                    .setCostGold(upgradeInfo.getGold())
                    .setGpGain(upgradeInfo.getGpPointReward())
                    .setSpeedWorker(upgradeInfo.getStonePerWorker().longValue())
                    .setMaxWorker(upgradeInfo.getMaxWorker())
            );
        }
        // @formatter:on
    }
}
