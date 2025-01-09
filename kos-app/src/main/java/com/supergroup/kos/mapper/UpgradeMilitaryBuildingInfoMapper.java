package com.supergroup.kos.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.MilitaryBuildingConfig;
import com.supergroup.kos.dto.building.BuildingDTO;
import com.supergroup.kos.dto.building.UpgradeMilitaryBuildingInfoResponse;

@Mapper
public interface UpgradeMilitaryBuildingInfoMapper {
    @Mapping(target = "duration", source = "militaryBuildingConfig.upgradeDuration")
    @Mapping(target = "reward.gloryPoint", source = "militaryBuildingConfig.gpPointReward")
    @Mapping(target = "requirement.wood", source = "militaryBuildingConfig.wood")
    @Mapping(target = "requirement.stone", source = "militaryBuildingConfig.stone")
    @Mapping(target = "requirement.gold", source = "militaryBuildingConfig.gold")
    @Mapping(target = "requirement.buildings", source = "militaryBuildingConfig.researchLevelRequired", qualifiedByName = "getBuildingRequirement")
    @Mapping(target = "percentDurationBuildShip", source = "militaryBuildingConfig.percentDurationBuildShip")

    UpgradeMilitaryBuildingInfoResponse toDTO(MilitaryBuildingConfig militaryBuildingConfig);

    List<UpgradeMilitaryBuildingInfoResponse> toDTOs(List<MilitaryBuildingConfig> queenBuildingConfigs);

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
