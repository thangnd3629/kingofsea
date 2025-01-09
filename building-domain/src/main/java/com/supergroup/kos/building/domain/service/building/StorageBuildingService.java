package com.supergroup.kos.building.domain.service.building;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetStorageBuildingCommand;
import com.supergroup.kos.building.domain.command.GetUpgradeInfoCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.StorageType;
import com.supergroup.kos.building.domain.model.building.StorageBuilding;
import com.supergroup.kos.building.domain.model.building.StorageBuildingConfig;
import com.supergroup.kos.building.domain.model.config.BaseBuildingConfig;
import com.supergroup.kos.building.domain.repository.persistence.asset.AssetsRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.building.StorageBuildingRepository;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.technology.TechnologyService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Service
@RequiredArgsConstructor
public class StorageBuildingService {

    @Delegate
    private final StorageBuildingRepository storageBuildingRepository;
    private final BuildingConfigDataSource  buildingConfigDataSource;
    private final AssetsRepository          assetsRepository;
    private final TechnologyService         technologyService;
    private final KosProfileService         kosProfileService;

    /**
     * get building info
     */
    @Transactional
    public StorageBuilding getBuilding(GetStorageBuildingCommand command) {
        var building = findByKosProfileIdAndStorageType(command.getKosProfileId(), command.getType())
                .orElseThrow(() -> KOSException.of(ErrorCode.STORAGE_BUILDING_NOT_FOUND));

        if (Objects.isNull(command.getCheckValidUnlock()) || command.getCheckValidUnlock()) {
            building.validUnlockBuilding(technologyService);
        } else {
            try {
                building.validUnlockBuilding(technologyService);
            } catch (Exception e) {
                if (e instanceof KOSException && ((KOSException) e).getCode().equals(ErrorCode.BUILDING_IS_LOCKED)) {
                    return building.setCapacity(0L).setAmount(0D);
                }
            }
        }

        var config = (StorageBuildingConfig) buildingConfigDataSource.getConfig(building.getName(), building.getLevel());
        var asset = assetsRepository.findByKosProfile_Id(command.getKosProfileId())
                                    .orElseThrow(() -> KOSException.of(ErrorCode.KOS_ASSETS_NOT_FOUND));
        var bonus = 0.0;
        switch (command.getType()) {
            case GOLD:
                building.setAmount(asset.getGold());
                bonus = building.getKosProfile().getBonusCapGoldStoragePercent();
                break;
            case STONE:
                building.setAmount(asset.getStone());
                bonus = building.getKosProfile().getBonusCapStoneStoragePercent();
                break;
            case WOOD:
                building.setAmount(asset.getWood());
                bonus = building.getKosProfile().getBonusCapWoodStoragePercent();
                break;
        }
        building.setCapacity(Double.valueOf(config.getCapacity().longValue() * (1 + bonus)).longValue());
        return building;
    }

    /**
     * Get upgrade research building config by level
     */
    public BaseBuildingConfig getUpgradeInfo(GetUpgradeInfoCommand command, StorageType type) {
        var kosProfile = kosProfileService.findById(command.getKosProfileId())
                                          .orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND));
        BuildingName name;
        switch (type) {
            case GOLD:
                name = BuildingName.STORAGE_GOLD;
                break;
            case STONE:
                name = BuildingName.STORAGE_STONE;
                break;
            case WOOD:
                name = BuildingName.STORAGE_WOOD;
                break;
            default:
                throw KOSException.of(ErrorCode.CONFIG_NOT_FOUND);
        }
        var res = buildingConfigDataSource.getConfig(name, command.getLevel());
        var duration = Double.valueOf(res.getUpgradeDuration() * (1 - kosProfile.getReduceUpgradingTimePercent())).longValue();
        res.setUpgradeDuration(duration);
        return res;
    }

    /**
     * Get upgrade research building config by level
     */
    public List<StorageBuildingConfig> getAllUpgradeInfo(StorageType type, Long kosProfileId) {
        var kosProfile = kosProfileService.findById(kosProfileId)
                                          .orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND));
        BuildingName name;
        switch (type) {
            case GOLD:
                name = BuildingName.STORAGE_GOLD;
                break;
            case STONE:
                name = BuildingName.STORAGE_STONE;
                break;
            case WOOD:
                name = BuildingName.STORAGE_WOOD;
                break;
            default:
                throw KOSException.of(ErrorCode.CONFIG_NOT_FOUND);
        }
        var listConfig = (List<StorageBuildingConfig>) buildingConfigDataSource.getListConfig(name);

        for (StorageBuildingConfig config : listConfig) {
            var upgradeDuration = Double.valueOf(config.getUpgradeDuration() * (1 - kosProfile.getReduceUpgradingTimePercent())).longValue();
            config.setUpgradeDuration(upgradeDuration);
        }

        return listConfig;
    }

}
