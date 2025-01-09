package com.supergroup.kos.building.domain.service.seamap.activity.arrivalHandler;

import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;

public interface MoveSessionHandler<T extends SeaElement> {
    void handleMove(T element, MoveSession session, SeaActivity activity);
}
