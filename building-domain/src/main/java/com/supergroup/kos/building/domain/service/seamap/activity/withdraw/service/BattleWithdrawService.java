package com.supergroup.kos.building.domain.service.seamap.activity.withdraw.service;

import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.BattleWithdrawalAsyncTask;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.constant.battle.FactionType;
import com.supergroup.kos.building.domain.dto.battle.EndBattleEvent;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRepository.CountLineUpInBattle;
import com.supergroup.kos.building.domain.service.battle.BattleHandler;
import com.supergroup.kos.building.domain.service.battle.BattlePendingWithdrawalService;
import com.supergroup.kos.building.domain.service.battle.ModeBattleHandler;
import com.supergroup.kos.building.domain.service.seamap.activity.ActivityRewardService;
import com.supergroup.kos.building.domain.service.seamap.activity.LineUpService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BattleWithdrawService {
    private final BattleRepository               battleRepository;
    private final ActivityRewardService          activityRewardService;
    private final LineUpService                  lineUpService;
    private final BattleWithdrawalAsyncTask      battleWithdrawalAsyncTask;
    private final BattlePendingWithdrawalService battlePendingWithdrawalService;
    private final ApplicationEventPublisher      publisher;

    public void withdrawFromBattle(SeaElement element, SeaActivity activity) {
        Battle battleOnSite = element.getBattle();
        ShipLineUp lineUp = activity.getLineUp();
        if (Objects.isNull(lineUp)){
            return;
        }

        // if user doesn't join any battle on target element
        if (Objects.isNull(battleOnSite) || Objects.isNull(lineUp.getBattleProfile()) || !lineUp.getBattleProfile().getBattle().getId().equals(
                battleOnSite.getId())) {
            return;
        }

        // user currently join in battle
        boolean canWithdraw = checkNeedWithdrawal(lineUp);
        if (!canWithdraw) {
            battlePendingWithdrawalService.enqueueWithdrawalTask(battleOnSite.getId(), lineUp.getId());
            battleWithdrawalAsyncTask.sendWithdrawNextRoundNotification(activity.getKosProfile().getUser());
            throw KOSException.of(ErrorCode.CAN_NOT_WITHDRAW);
        }

        if (battleOnSite.getStatus().equals(BattleStatus.END)) {
            var battleReward = battleOnSite.getBattleReport().getReward();
            activityRewardService.loadOnShip(activity, battleReward);
        }
        lineUp.setBattleProfile(null);
        lineUpService.save(lineUp);
        CountLineUpInBattle countCommanderLineUp = battleRepository.countCommanderLineUp(battleOnSite.getId());
        if (countCommanderLineUp.getCountLineUpAttacker() == 0) {
            publisher.publishEvent(new EndBattleEvent(battleOnSite.getId(), FactionType.DEFENDER));
        } else if (countCommanderLineUp.getCountLineUpDefender() == 0) {
            publisher.publishEvent(new EndBattleEvent(battleOnSite.getId(), FactionType.ATTACKER));
        }
    }

    private boolean checkNeedWithdrawal(ShipLineUp shipLineUp) {
        if (Objects.isNull(shipLineUp.getBattleProfile()) || Objects.isNull(shipLineUp.getTimeJoinedBattle())) {
            return true;
        }
        Battle battle = shipLineUp.getBattleProfile().getBattle();
        if (battle.getStatus().equals(BattleStatus.PROGRESS)) {
            if (Objects.nonNull(battle.getTimeUpdateStatus())) {
                // throw Ex  need
                return shipLineUp.getTimeJoinedBattle().isAfter(battle.getTimeUpdateStatus());
            }
        }
        return true;
    }
}
