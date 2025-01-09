package com.supergroup.kos.building.domain.service.seamap.activity.arrivalHandler;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.constant.seamap.SeaActivityStatus;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class AnchorEntryHandler implements MoveSessionHandler {
    private final SeaActivityService seaActivityService;
    @Override
    public void handleMove(SeaElement element, MoveSession session, SeaActivity activity) {
        activity.setStatus(SeaActivityStatus.ANCHORING);
        seaActivityService.save(activity);
    }
}
