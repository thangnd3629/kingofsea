package com.supergroup.kos.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.LighthouseBuildingConfig;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.dto.building.BuildingDTO;
import com.supergroup.kos.dto.building.LightHouseUpgradeInfoResponse;
import com.supergroup.kos.dto.seamap.activity.SeaActivityDTO;

@Mapper
public interface LightHouseUpgradeInfoMapper {
    @Mapping(source = "maxActionPoint", target = "reward.actionPoint")
    @Mapping(source = "wood", target = "requirement.wood")
    @Mapping(source = "gold", target = "requirement.gold")
    @Mapping(source = "stone", target = "requirement.stone")
    @Mapping(source = "gpPointReward", target = "reward.gloryPoint")
    @Mapping(target = "duration", source = "upgradeDuration")
    @Mapping(target = "requirement.buildings", source = "levelHeadquarter", qualifiedByName = "getBuildingRequirement")
    LightHouseUpgradeInfoResponse toDTO(LighthouseBuildingConfig model);


    @Named("getBuildingRequirement")
    default List<BuildingDTO> getBuildingRequirement(Long level) {
        if (level == null) {
            return null;
        }
        var buildingRequirements = new ArrayList<BuildingDTO>();
        buildingRequirements.add(new BuildingDTO().setName(BuildingName.CASTLE).setLevel(level));
        return buildingRequirements;
    }

    Collection<LightHouseUpgradeInfoResponse> toDTOs(Collection<LighthouseBuildingConfig> models);
}
