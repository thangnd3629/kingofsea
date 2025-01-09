package com.supergroup.kos.building.domain.consumer;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.supergroup.kos.building.domain.dto.seamap.CreateMoveSessionEvent;
import com.supergroup.kos.building.domain.dto.seamap.DeleteCacheElementEvent;
import com.supergroup.kos.building.domain.dto.seamap.SaveUpdateCacheElementEvent;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;
import com.supergroup.kos.building.domain.service.seamap.activity.MoveSessionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MoveSessionEventConsumer {

    private final MoveSessionService moveSessionService;
    private final SeaElementService  seaElementService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CreateMoveSessionEvent event) throws JsonProcessingException {
        log.info("Create move session event");
        moveSessionService.sendToQueue(event.getMoveSession());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void writeSeaElementCache(SaveUpdateCacheElementEvent event) {
        seaElementService.saveToCache(event.getSeaElement());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteSeaElementCache(DeleteCacheElementEvent event) {
        seaElementService.deleteByIdCache(event.getSeaElementId());
    }
}
