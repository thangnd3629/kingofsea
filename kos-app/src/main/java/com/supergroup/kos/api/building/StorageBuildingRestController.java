package com.supergroup.kos.api.building;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetStorageBuildingCommand;
import com.supergroup.kos.building.domain.command.GetUpgradeInfoCommand;
import com.supergroup.kos.building.domain.command.GetUpgradeStatusCommand;
import com.supergroup.kos.building.domain.command.GetVaultBuildingInfo;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.UpgradeBuildingCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.StorageType;
import com.supergroup.kos.building.domain.model.building.StorageBuildingConfig;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.StorageBuildingService;
import com.supergroup.kos.building.domain.service.building.VaultBuildingService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.upgrade.UpgradeService;
import com.supergroup.kos.dto.building.Storage;
import com.supergroup.kos.dto.building.StorageBuildingInfoResponse;
import com.supergroup.kos.dto.building.StupidStorageUpgradeInfoResponse;
import com.supergroup.kos.dto.upgrade.UpgradeStatusResponse;
import com.supergroup.kos.mapper.UpgradeSessionMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/building/storage")
@RequiredArgsConstructor
public class StorageBuildingRestController {

    private final StorageBuildingService storageBuildingService;
    private final KosProfileService      kosProfileService;
    private final AssetsService          assetsService;
    private final UpgradeService         upgradeService;
    private final UpgradeSessionMapper   upgradeSessionMapper;
    private final VaultBuildingService   vaultBuildingService;

    @GetMapping("{type}")
    private ResponseEntity<StorageBuildingInfoResponse> getBuildingInfo(@PathVariable String type) {
        // @formatter:off
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        StorageType storageType = StorageType.valueOf(type.toUpperCase());
        var building = storageBuildingService.getBuilding(new GetStorageBuildingCommand(storageType, kosProfileId));
        var protectCap = 0L;
        try {
            var vaultBuilding = vaultBuildingService.getBuildingInfo(new GetVaultBuildingInfo(kosProfileId));
            protectCap = Double.valueOf(building.getAmount() * vaultBuilding.getProtectPercent()).longValue();
        } catch (KOSException kosException) {
            if (kosException.getCode().equals(ErrorCode.BUILDING_IS_LOCKED)) {
                // ignore
            } else {
                throw kosException;
            }
        }
        return ResponseEntity.ok(new StorageBuildingInfoResponse().setStorage(new Storage().setCurrent(building.getAmount().longValue())
                                                                                           .setMax(building.getCapacity()))
                                                                  .setDescription("This is storage building")
                                                                  .setLevel(building.getLevel())
                                                                  .setProtectionCap(protectCap)
                                                                  .setLootableCap(Math.max(0, building.getAmount().longValue() - protectCap)));
        // @formatter:on
    }

    @PostMapping("/upgrade/{type}")
    public ResponseEntity<?> upgrade(@PathVariable String type) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        StorageType storageType = StorageType.valueOf(type.toUpperCase());
        var building = storageBuildingService.getBuilding(new GetStorageBuildingCommand(storageType, kosProfile.getId()));
        var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
        upgradeService.upgrade(new UpgradeBuildingCommand(kosProfile, building, asset));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/upgrade/{type}/status")
    public ResponseEntity<UpgradeStatusResponse> upgradeStatus(@PathVariable String type) {
        StorageType storageType = StorageType.valueOf(type.toUpperCase());
        BuildingName buildingName = null;
        switch (storageType) {
            case WOOD:
                buildingName = BuildingName.STORAGE_WOOD;
                break;
            case STONE:
                buildingName = BuildingName.STORAGE_STONE;
                break;
            case GOLD:
                buildingName = BuildingName.STORAGE_GOLD;
                break;
        }
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        UpgradeSession upgradeSession = upgradeService.getUpgradeSession(new GetUpgradeStatusCommand().setKosProfileId(kosProfile.getId())
                                                                                                      .setBuildingName(buildingName));
        return ResponseEntity.ok(upgradeSessionMapper.toUpgradeStatusResponse(upgradeSession));
    }

    @GetMapping("/upgrade/{type}")
    public ResponseEntity<?> getUpgradeInfo(@RequestParam(value = "level", required = false) Long level, @PathVariable String type) {
        // @formatter:off
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        StorageType storageType = StorageType.valueOf(type.toUpperCase());
        if (Objects.isNull(level)) {
            var upgradeInfos = storageBuildingService.getAllUpgradeInfo(storageType, kosProfile.getId());
            var listInfoResponse = upgradeInfos.stream().map(u -> new StupidStorageUpgradeInfoResponse().setTimeUpgrade(u.getUpgradeDuration())
                                                                                                        .setLevel(u.getLevel())
                                                                                                        .setCapacity(u.getCapacity().longValue())
                                                                                                        .setLevelHeadquarter(u.getLevelHeadquarters())
                                                                                                        .setCostGold(u.getGold())
                                                                                                        .setCostStone(u.getStone())
                                                                                                        .setCostWood(u.getWood())
                                                                                                        .setGpGain(u.getGpPointReward())).collect(Collectors.toList());
            return ResponseEntity.ok(Map.of("details", listInfoResponse));
        } else {
            var upgradeInfo = (StorageBuildingConfig) storageBuildingService.getUpgradeInfo(new GetUpgradeInfoCommand(level, kosProfile.getId()), storageType);
            return ResponseEntity.ok(new StupidStorageUpgradeInfoResponse()
                                             .setTimeUpgrade(upgradeInfo.getUpgradeDuration())
                                             .setLevel(upgradeInfo.getLevel())
                                             .setCapacity(upgradeInfo.getCapacity().longValue())
                                             .setLevelHeadquarter(upgradeInfo.getLevelHeadquarters())
                                             .setCostStone(upgradeInfo.getStone())
                                             .setCostWood(upgradeInfo.getWood())
                                             .setCostGold(upgradeInfo.getGold())
                                             .setGpGain(upgradeInfo.getGpPointReward()));
        }
        // @formatter:on
    }
}
