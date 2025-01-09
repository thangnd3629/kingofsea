package com.supergroup.kos.building.domain.service.seamap.activity.arrivalHandler;

import org.springframework.transaction.annotation.Transactional;

import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaActivityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public abstract class ArrivalHandler<T extends SeaElement> implements MoveSessionHandler<T> {

    protected final SeaActivityRepository seaActivityRepository;

    /**
     * Handle when activity arrival sea element
     */
    @Override
    @Transactional
    public void handleMove(T element, MoveSession session, SeaActivity activity) {
        log.info("Move session {} arrival and station at {}", session.getId(), element.getId());
        // set station for activity
        activity.setStationAt(element);
        seaActivityRepository.save(activity);
    }
}
