package com.supergroup.kos.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.supergroup.kos.building.domain.model.mining.QueenBuilding;
import com.supergroup.kos.building.domain.model.queen.Queen;
import com.supergroup.kos.dto.building.QueenBuildingResponse;

@Mapper
public interface QueenBuildingMapper {

    @Mapping(target = "numberOfQueen", source = "queenBuilding.ownQueens", qualifiedByName = "getNumberOfQueen")
    QueenBuildingResponse toDTO(QueenBuilding queenBuilding);

    List<QueenBuildingResponse> toDTOs(List<QueenBuilding> queenBuilding);
    @Named("getNumberOfQueen")
    default Long getNumberOfQueen(Collection<Queen> queens) {
        return (long) queens.size();
    }

}
