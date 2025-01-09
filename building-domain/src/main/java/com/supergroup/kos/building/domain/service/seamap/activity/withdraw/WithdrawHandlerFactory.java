package com.supergroup.kos.building.domain.service.seamap.activity.withdraw;

import org.springframework.stereotype.Component;

import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.service.seamap.activity.withdraw.elementHandler.BossBattleWithdrawHandler;
import com.supergroup.kos.building.domain.service.seamap.activity.withdraw.elementHandler.DefaultWithdrawHandler;
import com.supergroup.kos.building.domain.service.seamap.activity.withdraw.elementHandler.MiningWithdrawHandler;
import com.supergroup.kos.building.domain.service.seamap.activity.withdraw.elementHandler.UserBaseWithdrawHandler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class WithdrawHandlerFactory {
    private final MiningWithdrawHandler   miningWithdrawHandler;
    private final UserBaseWithdrawHandler userbaseWithdrawHandler;
    private final DefaultWithdrawHandler  defaultWithdrawHandler;
    private final BossBattleWithdrawHandler bossBattleWithdrawHandler;

    public WithdrawHandler getInstance(SeaElementType type) { // todo : opt for other ways than using raw type
        switch (type) {
            case RESOURCE:
                return miningWithdrawHandler;
            case USER_BASE:
                return userbaseWithdrawHandler;
            case BOSS:
                return bossBattleWithdrawHandler;
            default:
                return defaultWithdrawHandler;
        }
    }
}
