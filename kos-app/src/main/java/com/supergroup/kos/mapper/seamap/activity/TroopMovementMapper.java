package com.supergroup.kos.mapper.seamap.activity;

import java.util.Collection;
import java.util.stream.Collectors;

import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;
import com.supergroup.kos.dto.seamap.activity.TroopMovementDTO;

public interface TroopMovementMapper {

    TroopMovementDTO toDto(MoveSession moveSession);
    default Collection<TroopMovementDTO> toDtos(Collection<MoveSession> moveSessions){
        return moveSessions.stream().map(this::toDto).collect(Collectors.toList());
    }
}
