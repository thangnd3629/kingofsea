package com.supergroup.kos.building.domain.service.seamap.activity.arrivalHandler;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.MotherShipServiceAsyncTask;
import com.supergroup.kos.building.domain.async.SeaActivityAsyncTask;
import com.supergroup.kos.building.domain.constant.seamap.SeaActivityStatus;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;
import com.supergroup.kos.building.domain.service.scout.ScoutService;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;
import com.supergroup.kos.building.domain.service.seamap.activity.LineUpService;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;
import com.supergroup.kos.building.domain.service.seamap.reward.SeaRewardClaimer;
import com.supergroup.kos.building.domain.service.ship.MotherShipService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReturnBaseHandler implements MoveSessionHandler<UserBase> {
    private final SeaActivityService         activityService;
    private final ScoutService               scoutService;
    private final SeaRewardClaimer           seaRewardClaimer;
    private final LineUpService              lineUpService;
    private final SeaActivityAsyncTask       seaActivityAsyncTask;
    private final UserBaseService            userBaseService;
    private final OccupiedBaseEntryHandler   occupiedBaseEntryHandler;
    private final MotherShipService          motherShipService;
    private final MotherShipServiceAsyncTask motherShipServiceAsyncTask;

    /**
     * handle when activity come back main base
     */
    @Override
    @Transactional
    public void handleMove(UserBase element, MoveSession session, SeaActivity activity) {
        UserBase userBase = userBaseService.getByKosProfileId(activity.getKosProfile().getId());
        if (!element.getId().equals(userBase.getId())) {
            occupiedBaseEntryHandler.handleMove(element, session, activity);
            return;
        }
        // reset activity to default and disable
        activity.setCurrentLocation(null);
        activity.setActiveMoveSession(null);
        activity.setIsDeleted(true);

        if (Objects.isNull(activity.getLineUp())) {
            // claim scout report
            if (Objects.isNull(activity.getScout())) {
                throw KOSException.of(ErrorCode.SHIP_LINE_UP_NOT_FOUND);
            }
            scoutService.finalScout(activity.getScout().getId());
        } else {
            // finish line up mission
            ShipLineUp lineUp = activity.getLineUp();
            BattleProfile battleProfile = userBaseService.fightingOnMyBase(activity.getKosProfile().getId());
            if (Objects.nonNull(battleProfile)) {
                lineUp.setBattleProfile(battleProfile).setTimeJoinedBattle(LocalDateTime.now());
                lineUp.getMotherShip().setStatus(SeaActivityStatus.DEFENDING);
                lineUpService.save(lineUp);
            } else {
                lineUp.getMotherShip().setStatus(SeaActivityStatus.STANDBY);
                lineUpService.onFinishMission(activity.getLineUp());
            }
        }
        if (Objects.nonNull(activity.getLoadedOnShipReward())) {
            // claim reward
            seaRewardClaimer.claimRewardOnBaseArrival(activity, activity.getLoadedOnShipReward());
        }
        activityService.save(activity);

        // if mother ship return to main base set arrival main base is now
        if (element.getId().equals(userBase.getId())) {
            // scouting do not have sea activity
            if (Objects.nonNull(session.getSeaActivity()) && Objects.nonNull(session.getSeaActivity().getLineUp())) {
                var motherShip = session.getSeaActivity().getLineUp().getMotherShip();
                motherShip.setArrivalMainBaseTime(LocalDateTime.now());
                motherShipService.save(motherShip);
            }
        }

        seaActivityAsyncTask.sendReturnBaseNotification(activity.getKosProfile().getUser().getId());
        motherShipServiceAsyncTask.sendHealingNotification(activity.getLineUp().getMotherShip());
    }
}