package com.supergroup.kos.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.supergroup.kos.building.domain.model.config.QueenConfig;
import com.supergroup.kos.dto.queen.QueenConfigResponse;

@Mapper
public interface QueenConfigMapper {

    QueenConfigResponse toDTO(QueenConfig queenConfig);

    List<QueenConfigResponse> toDTOs(List<QueenConfig> queenConfig);
}
