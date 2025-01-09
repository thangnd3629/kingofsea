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
import com.supergroup.kos.building.domain.command.GetVaultBuildingInfo;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.UpgradeBuildingCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.VaultBuildingConfig;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.VaultBuildingService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.upgrade.UpgradeService;
import com.supergroup.kos.dto.building.VaultBuildingResponse;
import com.supergroup.kos.dto.upgrade.UpgradeStatusResponse;
import com.supergroup.kos.mapper.UpgradeSessionMapper;
import com.supergroup.kos.mapper.UpgradeVaultBuildingInfoMapper;
import com.supergroup.kos.mapper.VaultBuildingMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/building/vault")
@RequiredArgsConstructor
public class VaultBuildingRestController {
    private final KosProfileService              kosProfileService;
    private final VaultBuildingService           vaultBuildingService;
    private final VaultBuildingMapper            vaultBuildingMapper;
    private final UpgradeVaultBuildingInfoMapper upgradeVaultBuildingInfoMapper;
    private final UpgradeService                 upgradeService;
    private final AssetsService                  assetsService;
    private final UpgradeSessionMapper           upgradeSessionMapper;

    @GetMapping("")
    public ResponseEntity<VaultBuildingResponse> getBuildingInfo() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var vaultBuilding = vaultBuildingService.getBuildingInfo(new GetVaultBuildingInfo(kosProfile.getId()));
        var vaultBuildingResponse = vaultBuildingMapper.toDTO(vaultBuilding).setProtectPercent(vaultBuilding.getProtectPercent());
        return ResponseEntity.ok(vaultBuildingResponse);
    }

    @PostMapping("/upgrade")
    public ResponseEntity<?> upgrade() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var vaultBuilding = vaultBuildingService.getBuildingInfo(new GetVaultBuildingInfo(kosProfile.getId()));
        var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
        upgradeService.upgrade(new UpgradeBuildingCommand(kosProfile, vaultBuilding, asset));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/upgrade")
    public ResponseEntity<?> getUpgradeInfo(@RequestParam(value = "level", required = false) Long level) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        if (Objects.isNull(level)) {
            var vaultBuildingConfigs = (List<VaultBuildingConfig>) vaultBuildingService.getAllUpgradeInfo(
                    new GetAllUpgradeInfoCommand(BuildingName.VAULT, kosProfile.getId()));
            return ResponseEntity.ok(Map.of("details", upgradeVaultBuildingInfoMapper.toDTOs(vaultBuildingConfigs)));
        } else {
            var vaultBuildingConfig = (VaultBuildingConfig) vaultBuildingService.getUpgradeInfo(new GetUpgradeInfoCommand(level, kosProfile.getId()));
            return ResponseEntity.ok(upgradeVaultBuildingInfoMapper.toDTO(vaultBuildingConfig));
        }
    }

    @GetMapping("/upgrade/status")
    public ResponseEntity<UpgradeStatusResponse> upgradeStatus() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        UpgradeSession upgradeSession = upgradeService.getUpgradeSession(new GetUpgradeStatusCommand().setKosProfileId(kosProfile.getId())
                                                                                                      .setBuildingName(BuildingName.VAULT));
        return ResponseEntity.ok(upgradeSessionMapper.toUpgradeStatusResponse(upgradeSession));
    }

}
