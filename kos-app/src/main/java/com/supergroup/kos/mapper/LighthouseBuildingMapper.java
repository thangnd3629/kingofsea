package com.supergroup.kos.mapper;

import java.util.Collection;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.supergroup.kos.building.domain.model.building.LighthouseBuilding;
import com.supergroup.kos.dto.building.LighthouseBuildingDTO;

@Mapper
public interface LighthouseBuildingMapper {
    @Mapping(source = "maxActionPoint", target = "actionPoint")
    LighthouseBuildingDTO toDto(LighthouseBuilding model);

    Collection<LighthouseBuildingDTO> toDTOs(Collection<LighthouseBuilding> models);
}
