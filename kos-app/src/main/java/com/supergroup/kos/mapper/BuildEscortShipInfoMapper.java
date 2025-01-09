package com.supergroup.kos.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.model.config.EscortShipConfig;
import com.supergroup.kos.dto.building.BuildingDTO;
import com.supergroup.kos.dto.ship.BuildEscortShipResponse;

@Mapper
public interface BuildEscortShipInfoMapper {
    @Mapping(target = "type", source = "escortShipConfig.type")
    @Mapping(target = "name", source = "escortShipConfig.type", qualifiedByName = "getName" )
    @Mapping(target = "duration", source = "escortShipConfig.buildDuration")
    @Mapping(target = "requirement.wood", source = "escortShipConfig", qualifiedByName = "getWoodRequirement")
    @Mapping(target = "requirement.stone", source = "escortShipConfig", qualifiedByName = "getStoneRequirement")
    @Mapping(target = "requirement.gold", source = "escortShipConfig", qualifiedByName = "getGoldRequirement")
    @Mapping(target = "requirement.buildings", source = "escortShipConfig.militaryLevelRequired", qualifiedByName = "getBuildingRequirement")
    BuildEscortShipResponse toDTO(EscortShipConfig escortShipConfig);
    List<BuildEscortShipResponse> toDTOs(List<EscortShipConfig> escortShipConfigs);

    @Named("getBuildingRequirement")
    default List<BuildingDTO> getBuildingRequirement(Long level) {
        if (level == null) {
            return null;
        }
        var buildingRequirements = new ArrayList<BuildingDTO>();
        buildingRequirements.add(new BuildingDTO().setName(BuildingName.MILITARY).setLevel(level));
        return buildingRequirements;
    }
    @Named("getName")
    default String getName(EscortShipType type) {
        return type.getEscortShipTypeName();
    }

    @Named("getWoodRequirement")
    default Long getWoodRequirement(EscortShipConfig config) {
        return Math.round(config.getWood() * config.getPercentRssBuild());
    }

    @Named("getStoneRequirement")
    default Long getStoneRequirement(EscortShipConfig config) {
        return Math.round(config.getStone() * config.getPercentRssBuild());
    }

    @Named("getGoldRequirement")
    default Long getGoldRequirement(EscortShipConfig config) {
        return Math.round(config.getGold() * config.getPercentRssBuild());
    }
}
