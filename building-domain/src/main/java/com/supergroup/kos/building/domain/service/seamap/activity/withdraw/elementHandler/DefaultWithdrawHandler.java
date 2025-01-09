package com.supergroup.kos.building.domain.service.seamap.activity.withdraw.elementHandler;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.service.seamap.activity.withdraw.WithdrawHandler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DefaultWithdrawHandler implements WithdrawHandler<SeaElement> {
    @Override
    public void cleanUpOnWithdraw(SeaElement element, SeaActivity activity) {
    }
}
