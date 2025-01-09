package com.supergroup.kos.mapper;

import java.util.Collection;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.supergroup.kos.building.domain.model.config.RelicConfig;
import com.supergroup.kos.dto.relic.RelicConfigResponse;

@Mapper
public interface RelicConfigMapper {
    @Mapping(target = "mp", source = "relicMpConfig.mp")
    RelicConfigResponse toDTO(RelicConfig config);


    @Mapping(target = "mp", source = "relicMpConfig.mp")
    Collection<RelicConfigResponse> toDTOS(Collection<RelicConfig> configs);
}
