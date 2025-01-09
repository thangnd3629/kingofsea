package com.supergroup.kos.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.MotherShipQualityConfig;
import com.supergroup.kos.dto.building.BuildingDTO;
import com.supergroup.kos.dto.ship.UpgradeQualityMotherShipResponse;

@Mapper
public interface UpgradeQualityMotherShipMapper {

    @Mapping(target = "duration", source = "upgradeDuration")
    @Mapping(target = "requirement.buildings", source = "commandBuildingConfig.level",
             qualifiedByName = "commandBuildingRequirement")
    @Mapping(target = "requirement.gold", source = "gold")
    @Mapping(target = "requirement.wood", source = "wood")
    @Mapping(target = "requirement.stone", source = "stone")
    UpgradeQualityMotherShipResponse toDTO(MotherShipQualityConfig motherShipQualityConfig);

    List<UpgradeQualityMotherShipResponse> toDTOs(List<MotherShipQualityConfig> motherShipQualityConfig);

    @Named("commandBuildingRequirement")
    default List<BuildingDTO> commandBuildingRequirement(Long level) {
        var buildingRequirements = new ArrayList<BuildingDTO>();
        buildingRequirements.add(new BuildingDTO().setLevel(level).setName(BuildingName.COMMAND));
        return buildingRequirements;
    }
}
