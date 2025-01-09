package com.supergroup.kos.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.MotherShipConfigQualityConfig;
import com.supergroup.kos.dto.building.BuildingDTO;
import com.supergroup.kos.dto.ship.MotherShipConfigQualityConfigResponse;

@Mapper
public interface MotherShipConfigQualityConfigMapper {
    @Mapping(target = "buyRequirement.gold", source = "config.gold")
    @Mapping(target = "buyRequirement.buildings", source = "config.motherShipQualityConfig.commandBuildingConfig.level", qualifiedByName = "commandLevelRequired")
    @Mapping(target = "model", source = "config.motherShipConfig")
    @Mapping(target = "qualityInfo", source = "config.motherShipQualityConfig")
    MotherShipConfigQualityConfigResponse toDTO(MotherShipConfigQualityConfig config);

    List<MotherShipConfigQualityConfigResponse> toDTOs(List<MotherShipConfigQualityConfig> configs);
    @Named("commandLevelRequired")
    default List<BuildingDTO> commandLevelRequired(Long level) {
        if(Objects.isNull(level)) {
            return null;
        }
        var buildings = new ArrayList<BuildingDTO>();
        buildings.add(new BuildingDTO().setName(BuildingName.COMMAND).setLevel(level));
        return buildings;
    }
}
