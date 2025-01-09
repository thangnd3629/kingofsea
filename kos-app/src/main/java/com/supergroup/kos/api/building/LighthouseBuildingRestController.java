package com.supergroup.kos.api.building;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.kos.building.domain.command.GetAllUpgradeInfoCommand;
import com.supergroup.kos.building.domain.command.GetUpgradeInfoCommand;
import com.supergroup.kos.building.domain.command.GetUpgradeStatusCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.UpgradeBuildingCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.LighthouseBuildingConfig;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.LighthouseBuildingService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.upgrade.UpgradeService;
import com.supergroup.kos.dto.building.LighthouseBuildingDTO;
import com.supergroup.kos.dto.upgrade.UpgradeStatusResponse;
import com.supergroup.kos.mapper.LighthouseBuildingMapper;
import com.supergroup.kos.mapper.LightHouseUpgradeInfoMapper;
import com.supergroup.kos.mapper.UpgradeSessionMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/building/lighthouse")
@RequiredArgsConstructor
public class LighthouseBuildingRestController {
    private final KosProfileService         kosProfileService;
    private final LighthouseBuildingMapper  lightHouseBuildingMapper;
    private final LighthouseBuildingService lightHouseBuildingService;
    private final LightHouseUpgradeInfoMapper lightHouseUpgradeInfoMapper;
    private final AssetsService               assetsService;
    private final UpgradeService              upgradeService;
    private final UpgradeSessionMapper        upgradeSessionMapper;

    @GetMapping("")
    public ResponseEntity<?> getBuildingInfo() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var lighthouseBuilding = lightHouseBuildingService.getBuildingInfo(kosProfile.getId());
        LighthouseBuildingDTO response = lightHouseBuildingMapper.toDto(lighthouseBuilding);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upgrade")
    public ResponseEntity<?> upgrade() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var lightHouseBuilding = lightHouseBuildingService.getBuildingInfo(kosProfile.getId());
        var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
        upgradeService.upgrade(new UpgradeBuildingCommand(kosProfile, lightHouseBuilding, asset));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/upgrade")
    public ResponseEntity<?> getUpgradeInfo(@RequestParam(value = "level", required = false) Long level) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        if (Objects.isNull(level)) {
            var lightHouseBuildingConfigs = (List<LighthouseBuildingConfig>) lightHouseBuildingService.getAllUpgradeInfo(
                    new GetAllUpgradeInfoCommand(BuildingName.LIGHTHOUSE, kosProfile.getId()));
            return ResponseEntity.ok(Map.of("details", lightHouseUpgradeInfoMapper.toDTOs(lightHouseBuildingConfigs)));
        } else {
            var lightHouseBuildingConfig = (LighthouseBuildingConfig) lightHouseBuildingService.getUpgradeInfo(
                    new GetUpgradeInfoCommand(level, kosProfile.getId()));
            return ResponseEntity.ok(lightHouseUpgradeInfoMapper.toDTO(lightHouseBuildingConfig));
        }
    }

    @GetMapping("/upgrade/status")
    public ResponseEntity<UpgradeStatusResponse> upgradeStatus() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        UpgradeSession upgradeSession = upgradeService.getUpgradeSession(new GetUpgradeStatusCommand().setKosProfileId(kosProfile.getId())
                                                                                                      .setBuildingName(BuildingName.LIGHTHOUSE));
        return ResponseEntity.ok(upgradeSessionMapper.toUpgradeStatusResponse(upgradeSession));
    }

}
