package com.supergroup.kos.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.MotherShipLevelConfig;
import com.supergroup.kos.dto.building.BuildingDTO;
import com.supergroup.kos.dto.ship.UpgradeLevelMotherShipResponse;

@Mapper
public interface UpgradeLevelMotherShipMapper {

    @Mapping(target = "duration", source = "motherShipLevelConfig.upgradeDuration")
    @Mapping(target = "requirement.wood", source = "motherShipLevelConfig.wood")
    @Mapping(target = "requirement.stone", source = "motherShipLevelConfig.stone")
    @Mapping(target = "requirement.gold", source = "motherShipLevelConfig.gold")
    @Mapping(target = "requirement.buildings", source = "motherShipLevelConfig.commandBuildingConfig.level",
             qualifiedByName = "commandBuildingRequirement")
    UpgradeLevelMotherShipResponse toDTO(MotherShipLevelConfig motherShipLevelConfig);

    List<UpgradeLevelMotherShipResponse> toDTOs(List<MotherShipLevelConfig> motherShipLevelConfigs);

    @Named("commandBuildingRequirement")
    default List<BuildingDTO> commandBuildingRequirement(Long level) {
        var buildingRequirements = new ArrayList<BuildingDTO>();
        buildingRequirements.add(new BuildingDTO().setLevel(level).setName(BuildingName.COMMAND));
        return buildingRequirements;
    }
}
