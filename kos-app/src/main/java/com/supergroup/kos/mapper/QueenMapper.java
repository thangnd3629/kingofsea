package com.supergroup.kos.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.supergroup.kos.building.domain.model.queen.Queen;
import com.supergroup.kos.dto.queen.QueenResponse;

@Mapper
public interface QueenMapper {

    @Mapping(target = "model.id", source = "queen.queenConfig.id")
    @Mapping(target = "model.name", source = "queen.queenConfig.name")
    @Mapping(target = "model.mp", source = "queen.queenConfig.mp")
    QueenResponse toDTO(Queen queen);

    List<QueenResponse> toDTOs(List<Queen> queen);
}
