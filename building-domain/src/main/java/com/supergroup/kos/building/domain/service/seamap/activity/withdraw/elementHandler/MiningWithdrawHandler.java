package com.supergroup.kos.building.domain.service.seamap.activity.withdraw.elementHandler;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.kos.building.domain.async.BattleWithdrawalAsyncTask;
import com.supergroup.kos.building.domain.model.seamap.ResourceIsland;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaActivityRepository;
import com.supergroup.kos.building.domain.service.seamap.activity.ActivityRewardService;
import com.supergroup.kos.building.domain.service.seamap.activity.withdraw.BaseWithdrawHandler;
import com.supergroup.kos.building.domain.service.seamap.activity.withdraw.service.BattleWithdrawService;
import com.supergroup.kos.building.domain.service.seamap.mining.ResourceMiningService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MiningWithdrawHandler extends BaseWithdrawHandler<ResourceIsland> {

    private final ResourceMiningService     resourceMiningService;
    private final ActivityRewardService     activityRewardService;
    private final BattleWithdrawService     battleWithdrawService;
    private final BattleWithdrawalAsyncTask battleWithdrawalAsyncTask;

    @Autowired
    public MiningWithdrawHandler(SeaActivityRepository seaActivityRepo,
                                 ResourceMiningService resourceMiningService,
                                 ActivityRewardService activityRewardService,
                                 BattleWithdrawService battleWithdrawService,
                                 BattleWithdrawalAsyncTask battleWithdrawalAsyncTask) {
        super(seaActivityRepo);
        this.resourceMiningService = resourceMiningService;
        this.activityRewardService = activityRewardService;
        this.battleWithdrawService = battleWithdrawService;
        this.battleWithdrawalAsyncTask = battleWithdrawalAsyncTask;
    }

    @Override
    @Transactional
    public void cleanUpOnWithdraw(ResourceIsland element, SeaActivity activity) {
        super.cleanUpOnWithdraw(element, activity);
        if (Objects.isNull(element.getMiningSession())) {
            return;
        }
        battleWithdrawService.withdrawFromBattle(element, activity);
        if (Objects.equals(element.getMiningSession().getSeaActivity().getId(), activity.getId())) {
            resourceMiningService.endSession(element.getMiningSession(), (reward) -> {
                activityRewardService.loadOnShip(activity, reward);
            });

        }

    }
}
