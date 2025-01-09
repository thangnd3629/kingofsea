package com.supergroup.kos.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.supergroup.kos.building.domain.model.relic.Relic;
import com.supergroup.kos.dto.relic.RelicResponse;

@Mapper
public interface RelicMapper {

    @Mapping(target = "model.id", source = "relic.relicConfig.id")
    @Mapping(target = "model.name", source = "relic.relicConfig.name")
    @Mapping(target = "model.level", source = "relic.relicConfig.relicMpConfig.level")
    @Mapping(target = "isListing", source = "relic.isListing")
    @Mapping(target = "model.mp", source = "relic.relicConfig.relicMpConfig.mp")
    RelicResponse toDTO(Relic relic);
    List<RelicResponse> toDTOs(List<Relic> relics);

}
