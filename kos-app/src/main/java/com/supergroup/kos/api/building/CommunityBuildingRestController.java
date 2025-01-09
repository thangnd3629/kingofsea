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
import com.supergroup.kos.building.domain.command.GetCommunityBuildingInfo;
import com.supergroup.kos.building.domain.command.GetUpgradeInfoCommand;
import com.supergroup.kos.building.domain.command.GetUpgradeStatusCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.UpgradeBuildingCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.CommunityBuildingConfig;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.CommunityBuildingService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.upgrade.UpgradeService;
import com.supergroup.kos.dto.building.CommunityBuildingResponse;
import com.supergroup.kos.dto.upgrade.UpgradeStatusResponse;
import com.supergroup.kos.mapper.CommunityBuildingMapper;
import com.supergroup.kos.mapper.UpgradeCommunityBuildingInfoMapper;
import com.supergroup.kos.mapper.UpgradeSessionMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/building/community")
@RequiredArgsConstructor
public class CommunityBuildingRestController {
    private final KosProfileService                  kosProfileService;
    private final CommunityBuildingService           communityBuildingService;
    private final AssetsService                      assetsService;
    private final UpgradeService                     upgradeService;
    private final CommunityBuildingMapper            communityBuildingMapper;
    private final UpgradeCommunityBuildingInfoMapper upgradeCommunityBuildingInfoMapper;
    private final UpgradeSessionMapper               upgradeSessionMapper;

    @GetMapping("")
    public ResponseEntity<CommunityBuildingResponse> getBuildingInfo() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var communityBuilding = communityBuildingService.getBuildingInfo(new GetCommunityBuildingInfo(kosProfile.getId()));
        var response = communityBuildingMapper.toDTO(communityBuilding);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upgrade")
    public ResponseEntity<?> upgrade() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var communityBuilding = communityBuildingService.getBuildingInfo(new GetCommunityBuildingInfo(kosProfile.getId()));
        var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
        upgradeService.upgrade(new UpgradeBuildingCommand(kosProfile, communityBuilding, asset));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/upgrade")
    public ResponseEntity<?> getUpgradeInfo(@RequestParam(value = "level", required = false) Long level) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        if (Objects.isNull(level)) {
            var command = new GetAllUpgradeInfoCommand(BuildingName.COMMUNITY, kosProfile.getId());
            var communityBuildingConfigs = (List<CommunityBuildingConfig>) communityBuildingService.getAllUpgradeInfo(command);
            return ResponseEntity.ok(Map.of("details", upgradeCommunityBuildingInfoMapper.toDTOs(communityBuildingConfigs)));
        } else {
            var communityBuildingConfig = (CommunityBuildingConfig) communityBuildingService.getUpgradeInfo(
                    new GetUpgradeInfoCommand(level, kosProfile.getId()));
            return ResponseEntity.ok(upgradeCommunityBuildingInfoMapper.toDTO(communityBuildingConfig));
        }
    }

    @GetMapping("/upgrade/status")
    public ResponseEntity<UpgradeStatusResponse> upgradeStatus() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        UpgradeSession upgradeSession = upgradeService.getUpgradeSession(new GetUpgradeStatusCommand().setKosProfileId(kosProfile.getId())
                                                                                                      .setBuildingName(BuildingName.COMMUNITY));
        return ResponseEntity.ok(upgradeSessionMapper.toUpgradeStatusResponse(upgradeSession));
    }
}
