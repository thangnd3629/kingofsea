package com.supergroup.kos.building.domain.service.seamap.activity.arrivalHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.model.seamap.movesession.MissionType;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaActivityRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserBaseEntryHandler extends ArrivalHandler<UserBase> {
    private final ScoutMoveSessionHandler  scoutMoveSessionHandler;
    private final ReturnBaseHandler        returnBaseHandler;
    private final CombatEntryHandler       combatEntryHandler;
    private final OccupiedBaseEntryHandler occupiedBaseEntryHandler;

    @Autowired
    public UserBaseEntryHandler(SeaActivityRepository seaActivityRepository,
                                ScoutMoveSessionHandler scoutMoveSessionHandler,
                                CombatEntryHandler combatEntryHandler,
                                ReturnBaseHandler returnBaseHandler,
                                OccupiedBaseEntryHandler occupiedBaseEntryHandler
                               ) {
        super(seaActivityRepository);
        this.scoutMoveSessionHandler = scoutMoveSessionHandler;
        this.returnBaseHandler = returnBaseHandler;
        this.combatEntryHandler = combatEntryHandler;
        this.occupiedBaseEntryHandler = occupiedBaseEntryHandler;
    }

    @Override
    public void handleMove(UserBase element, MoveSession session, SeaActivity activity) {
        super.handleMove(element, session, activity);
        MissionType missionType = session.getMissionType();
        switch (missionType) {
            case RETURN:
                returnBaseHandler.handleMove(element, session, activity);
                return;
            case SCOUT:
                scoutMoveSessionHandler.handleMove(element, session, activity);
                return;
            case ATTACK:
            case OCCUPY:
                combatEntryHandler.handleUserBaseCombatMove(element, session, activity);
                return;
            case STATION:
                occupiedBaseEntryHandler.handleMove(element, session, activity);
        }
    }
}

