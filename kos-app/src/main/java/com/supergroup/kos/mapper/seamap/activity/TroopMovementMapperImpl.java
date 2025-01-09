package com.supergroup.kos.mapper.seamap.activity;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.model.scout.Scout;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.model.seamap.movesession.MissionType;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaActivityRepository;
import com.supergroup.kos.dto.seamap.activity.ShipLineUpDTO;
import com.supergroup.kos.dto.seamap.activity.TroopMovementDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TroopMovementMapperImpl implements TroopMovementMapper {
    private final SeaActivityRepository seaActivityRepository;
    private final LineUpMapper          lineUpMapper;

    @Override
    public TroopMovementDTO toDto(MoveSession moveSession) {
        SeaActivity activity = seaActivityRepository.getActivityByMoveSession(moveSession.getId());
        ShipLineUpDTO shipLineUpDTO = new ShipLineUpDTO();
        ShipLineUp lineUp = activity.getLineUp();
        if (Objects.nonNull(lineUp)) {
            shipLineUpDTO = lineUpMapper.toDto(lineUp);
            shipLineUpDTO.setShipUnits(moveSession.getShipUnits());
        }
        if (Objects.nonNull(activity.getScout())) {
            Scout scout = activity.getScout();
            if (moveSession.getMissionType().equals(MissionType.RETURN)) {
                shipLineUpDTO.setNumberArmy(scout.getSoliderRemain());
            } else {
                shipLineUpDTO.setNumberArmy(scout.getNumberArmy());
            }
        }
        TroopMovementDTO result = new TroopMovementDTO();
        result.setStart(moveSession.getStart())
              .setEnd(moveSession.getEnd())
              .setId(moveSession.getId())
              .setSpeed(moveSession.getSpeed())
              .setTimeStart(moveSession.getTimeStart())
              .setSeaElementType(moveSession.getDestinationType())
              .setMissionType(moveSession.getMissionType())
              .setLineUp(shipLineUpDTO);
        return result;
    }
}
