package com.supergroup.kos.building.domain.mapper;

import java.util.Objects;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import com.supergroup.kos.building.domain.constant.seamap.MoveSessionType;
import com.supergroup.kos.building.domain.dto.seamap.MoveSessionDTO;
import com.supergroup.kos.building.domain.model.seamap.movesession.MissionType;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;

@Mapper
public interface MoveSessionMapper {
    @Mappings({
            @Mapping(source = "seaActivity.id", target = "seaActivityId")
    })
    MoveSessionDTO toDTO(MoveSession moveSession);

    @AfterMapping
    default void assignType(@MappingTarget MoveSessionDTO moveSessionDTO, MoveSession moveSession) {
        moveSessionDTO.setDestinationType(moveSession.getDestinationType());
        MissionType missionType = moveSessionDTO.getMissionType();
        switch (moveSession.getDestinationType()) {
            case RESOURCE:
                if (missionType.equals(MissionType.SCOUT)) {
                    moveSessionDTO.setType(MoveSessionType.SCOUT);
                } else {moveSessionDTO.setType(MoveSessionType.MINING);}
                break;
            case BOSS:
                moveSessionDTO.setType(MoveSessionType.BOSS_BATTLE);
                break;
            case USER_BASE:
                if (moveSession.getMissionType().equals(MissionType.SCOUT)) {
                    moveSessionDTO.setType(MoveSessionType.SCOUT);
                } else {
                    moveSessionDTO.setType(MoveSessionType.USER_BATTLE);
                }
                break;
        }
        if (moveSession.getMissionType().equals(MissionType.RETURN)) {
            if (Objects.isNull(moveSession.getSeaActivity().getLineUp())) {
                moveSessionDTO.setType(MoveSessionType.SCOUT);
            } else {
                moveSessionDTO.setType(MoveSessionType.RETURN_BASE);
            }
        }

    }
}
