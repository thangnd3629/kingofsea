package com.supergroup.kos.building.domain.service.seamap.activity.arrivalHandler;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MoveSessionHandlerFactory {
    private final MineEntryHandler             mineEntryHandler;
    private final UserBaseEntryHandler         userBaseEntryHandler;
    private final BossBattleMoveSessionHandler bossBattleMoveSessionHandler;

    public MoveSessionHandler getInstance(SeaElementType type) {
        switch (type) {
            case RESOURCE:
                return mineEntryHandler;
            case USER_BASE:
                return userBaseEntryHandler;
            case BOSS:
                return bossBattleMoveSessionHandler;
            default:
                throw new NotImplementedException("This move session type is not implemented");
        }

    }
}
