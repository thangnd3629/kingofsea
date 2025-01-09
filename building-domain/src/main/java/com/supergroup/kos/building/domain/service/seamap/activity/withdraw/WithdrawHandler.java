package com.supergroup.kos.building.domain.service.seamap.activity.withdraw;

import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;

public interface WithdrawHandler<T extends SeaElement> {
    void cleanUpOnWithdraw(T element, SeaActivity activity);
}
