package com.supergroup.kos.building.domain.service.building;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetVaultBuildingInfo;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.building.VaultBuilding;
import com.supergroup.kos.building.domain.model.config.BaseBuildingConfig;
import com.supergroup.kos.building.domain.model.config.VaultBuildingConfig;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.building.VaultBuildingRepository;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.technology.TechnologyService;

import lombok.experimental.Delegate;

@Service
public class VaultBuildingService extends BaseBuildingService {
    @Delegate
    private final VaultBuildingRepository vaultBuildingRepository;

    private final BuildingConfigDataSource buildingConfigDataSource;
    private final TechnologyService        technologyService;

    public VaultBuildingService(@Autowired VaultBuildingRepository vaultBuildingRepository,
                                @Autowired BuildingConfigDataSource buildingConfigDataSource,
                                @Autowired TechnologyService technologyService,
                                @Autowired KosProfileService kosProfileService) {
        super(kosProfileService, buildingConfigDataSource);
        this.vaultBuildingRepository = vaultBuildingRepository;
        this.buildingConfigDataSource = buildingConfigDataSource;
        this.technologyService = technologyService;
    }

    /**
     * Get building info
     */
    public VaultBuilding getBuildingInfo(GetVaultBuildingInfo command) {
        var vaultBuilding = vaultBuildingRepository.findByKosProfileId(command.getKosProfileId())
                                                   .orElseThrow(() -> KOSException.of(ErrorCode.VAULT_BUILDING_IS_NOT_FOUND));

        if (command.getValidateBuilding()) {vaultBuilding.validUnlockBuilding(technologyService);}

        var vaultBuildingConfig = (VaultBuildingConfig) buildingConfigDataSource.getConfig(BuildingName.VAULT, vaultBuilding.getLevel());
        vaultBuilding.setProtectPercent(
                vaultBuildingConfig.getProtectPercent() * (1 + vaultBuilding.getKosProfile().getBonusProtectResourcePercent()));
        return vaultBuilding;
    }

    @Override
    protected BaseBuildingConfig getBuildingConfig(Long level) {
        return buildingConfigDataSource.getConfig(BuildingName.VAULT, level);
    }

}
