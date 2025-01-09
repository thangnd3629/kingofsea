package com.supergroup.kos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.supergroup.kos.building.domain.model.technology.UserTechnology;
import com.supergroup.kos.dto.technology.TechnologyDTO;

@Mapper
public interface TechnologyRequirementMapper {
    @Mapping(target = "name", source = "userTechnology.technology.name")
    @Mapping(target = "code", source = "userTechnology.technology.code")
    @Mapping(target = "type", source = "userTechnology.technology.technologyType")
    @Mapping(target = "isResearched", source = "userTechnology.isResearched")
    TechnologyDTO toDTO(UserTechnology userTechnology);
}
