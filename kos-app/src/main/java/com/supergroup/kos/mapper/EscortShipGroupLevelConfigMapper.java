package com.supergroup.kos.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.ArmoryBuildingConfig;
import com.supergroup.kos.building.domain.model.config.EscortShipGroupLevelConfig;
import com.supergroup.kos.dto.building.BuildingDTO;
import com.supergroup.kos.dto.ship.EscortShipGroupLevelConfigResponse;

@Mapper
public interface EscortShipGroupLevelConfigMapper {
    @Mapping(target = "quality", source = "level")
    @Mapping(target = "shipGroup", source = "escortShipGroupConfig.name")
    @Mapping(target = "requirement.wood", source = "wood")
    @Mapping(target = "requirement.stone", source = "stone")
    @Mapping(target = "requirement.gold", source = "gold")
    @Mapping(target = "requirement.buildings", source = "armoryBuildingConfig", qualifiedByName = "armoryLevelRequired")
    @Mapping(target = "percentStat", source = "percentStat")
    EscortShipGroupLevelConfigResponse toDTO(EscortShipGroupLevelConfig escortShipGroupLevelConfig);

    List<EscortShipGroupLevelConfigResponse> toDTOs(List<EscortShipGroupLevelConfig> escortShipGroupLevelConfigs);

    @Named("armoryLevelRequired")
    default List<BuildingDTO> armoryLevelRequired(ArmoryBuildingConfig level) {
        if (Objects.isNull(level)) {
            return null;
        }
        var buildings = new ArrayList<BuildingDTO>();
        buildings.add(new BuildingDTO().setName(BuildingName.ARMORY).setLevel(level.getLevel()));
        return buildings;
    }
}
