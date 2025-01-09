package com.supergroup.kos.building.domain.service.seamap.activity.withdraw.elementHandler;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.BossSea;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaActivityRepository;
import com.supergroup.kos.building.domain.service.seamap.activity.ActivityRewardService;
import com.supergroup.kos.building.domain.service.seamap.activity.withdraw.BaseWithdrawHandler;
import com.supergroup.kos.building.domain.service.seamap.activity.withdraw.WithdrawHandler;
import com.supergroup.kos.building.domain.service.seamap.activity.withdraw.service.BattleWithdrawService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BossBattleWithdrawHandler extends BaseWithdrawHandler<BossSea> {
    private final ActivityRewardService activityRewardService;
    private final BattleWithdrawService battleWithdrawService;

    @Autowired
    public BossBattleWithdrawHandler(ActivityRewardService activityRewardService, SeaActivityRepository seaActivityRepository,
                                     BattleWithdrawService battleWithdrawService) {
        super(seaActivityRepository);
        this.activityRewardService = activityRewardService;
        this.battleWithdrawService = battleWithdrawService;
    }

    @Override
    @Transactional
    public void cleanUpOnWithdraw(BossSea element, SeaActivity activity) {
        super.cleanUpOnWithdraw(element, activity);
        // validate battle
        Battle battle = element.getBattle();
        if (Objects.isNull(battle)) {
            return;
        }
        battleWithdrawService.withdrawFromBattle(element, activity);
    }
}
