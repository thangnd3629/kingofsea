

package com.supergroup.kos.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.ArmoryBuildingConfig;
import com.supergroup.kos.dto.building.BuildingDTO;
import com.supergroup.kos.dto.building.UpgradeArmoryBuildingInfoResponse;

@Mapper
public interface UpgradeArmoryBuildingInfoMapper {
    @Mapping(target = "duration", source = "armoryBuildingConfig.upgradeDuration")
    @Mapping(target = "reward.gloryPoint", source = "armoryBuildingConfig.gpPointReward")
    @Mapping(target = "reward.unLockWeaponSetLevel", source = "armoryBuildingConfig.unLockWeaponSetLevel")
    @Mapping(target = "reward.unLockEscortShipGroupLevel.quality", source = "armoryBuildingConfig.unLockEscortShipGroupLevel")
    @Mapping(target = "reward.unLockEscortShipGroupLevel.shipGroup", source = "armoryBuildingConfig.unLockEscortShipGroupName")
    @Mapping(target = "requirement.wood", source = "armoryBuildingConfig.wood")
    @Mapping(target = "requirement.stone", source = "armoryBuildingConfig.stone")
    @Mapping(target = "requirement.gold", source = "armoryBuildingConfig.gold")
    @Mapping(target = "requirement.buildings", source = "armoryBuildingConfig.researchLevelRequired", qualifiedByName = "getBuildingRequirement")
    UpgradeArmoryBuildingInfoResponse toDTO(ArmoryBuildingConfig armoryBuildingConfig);

    List<UpgradeArmoryBuildingInfoResponse> toDTOs(List<ArmoryBuildingConfig> armoryBuildingConfigs);
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
