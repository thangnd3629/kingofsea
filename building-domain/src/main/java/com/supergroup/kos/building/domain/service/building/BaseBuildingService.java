package com.supergroup.kos.building.domain.service.building;

import java.util.List;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetAllUpgradeInfoCommand;
import com.supergroup.kos.building.domain.command.GetUpgradeInfoCommand;
import com.supergroup.kos.building.domain.model.config.BaseBuildingConfig;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseBuildingService {

    private final   KosProfileService        kosProfileService;
    protected final BuildingConfigDataSource buildingConfigDataSource;

    protected abstract BaseBuildingConfig getBuildingConfig(Long level);

    public BaseBuildingConfig getUpgradeInfo(GetUpgradeInfoCommand command) {
        var config = getBuildingConfig(command.getLevel());
        var kosProfile = kosProfileService.findById(command.getKosProfileId()).orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND));
        validateUpgradeDuration(config, kosProfile);
        return config;
    }

    public List<? extends BaseBuildingConfig> getAllUpgradeInfo(GetAllUpgradeInfoCommand command) {
        var listConfig = buildingConfigDataSource.getListConfig(command.getName());
        var kosProfile = kosProfileService.findById(command.getKosProfileId()).orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND));
        for (BaseBuildingConfig config : listConfig) {
            validateUpgradeDuration(config, kosProfile);
        }
        return listConfig;
    }

    private void validateUpgradeDuration(BaseBuildingConfig config, KosProfile kosProfile) {
        var upgradeDuration = Double.valueOf(config.getUpgradeDuration() * (1 - kosProfile.getReduceUpgradingTimePercent())).longValue();
        config.setUpgradeDuration(upgradeDuration);
    }
}
