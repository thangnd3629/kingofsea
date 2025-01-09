package com.supergroup.kos.building.domain.service.seamap.activity.withdraw;

import java.util.Objects;

import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaActivityRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseWithdrawHandler<T extends SeaElement> implements WithdrawHandler<T> {

    protected final SeaActivityRepository seaActivityRepo;

    /**
     * Handle when activity start withdraw
     */
    @Override
    public void cleanUpOnWithdraw(T element, SeaActivity activity) {
        // remove station for activity
        if (Objects.isNull(element)){
            return;
        }
        activity.setStationAt(null);
        seaActivityRepo.save(activity);
    }
}
