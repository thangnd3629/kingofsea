package com.supergroup.kos.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.VaultBuildingConfig;
import com.supergroup.kos.dto.building.BuildingDTO;
import com.supergroup.kos.dto.building.UpgradeVaultBuildingInfoResponse;

@Mapper
public interface UpgradeVaultBuildingInfoMapper {
    @Mapping(target = "duration", source = "vaultBuildingConfig.upgradeDuration")
    @Mapping(target = "reward.gloryPoint", source = "vaultBuildingConfig.gpPointReward")
    @Mapping(target = "requirement.wood", source = "vaultBuildingConfig.wood")
    @Mapping(target = "requirement.stone", source = "vaultBuildingConfig.stone")
    @Mapping(target = "requirement.gold", source = "vaultBuildingConfig.gold")
    @Mapping(target = "requirement.buildings", source = "vaultBuildingConfig.researchLevelRequired", qualifiedByName = "getBuildingRequirement")
    UpgradeVaultBuildingInfoResponse toDTO(VaultBuildingConfig vaultBuildingConfig);
    List<UpgradeVaultBuildingInfoResponse> toDTOs(List<VaultBuildingConfig> vaultBuildingConfigs);
    @Named("getBuildingRequirement")
    default List<BuildingDTO> getBuildingRequirement(Long level) {
        if (level == null) {
            return null;
        }
        var buildingRequirements = new ArrayList<BuildingDTO>();
        buildingRequirements.add(new BuildingDTO().setName(BuildingName.RESEARCH).setLevel(level));
        return buildingRequirements;
    }
}

