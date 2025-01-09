package com.supergroup.kos.building.domain.service.seamap.activity.withdraw.elementHandler;

import com.supergroup.kos.building.domain.async.OccupyCombatAsyncTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.model.seamap.movesession.MissionType;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaActivityRepository;
import com.supergroup.kos.building.domain.service.battle.OccupyService;
import com.supergroup.kos.building.domain.service.seamap.MapService;
import com.supergroup.kos.building.domain.service.seamap.activity.withdraw.BaseWithdrawHandler;
import com.supergroup.kos.building.domain.service.seamap.activity.withdraw.service.BattleWithdrawService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserBaseWithdrawHandler extends BaseWithdrawHandler<UserBase> {

    private final BattleWithdrawService battleWithdrawService;
    private final OccupyService         occupyService;
    private final SeaActivityRepository seaActivityRepository;
    private final MapService            mapService;
    private final OccupyCombatAsyncTask occupyCombatAsyncTask;

    @Autowired
    public UserBaseWithdrawHandler(SeaActivityRepository seaActivityRepo,
                                   BattleWithdrawService battleWithdrawService,
                                   OccupyService occupyService,
                                   SeaActivityRepository seaActivityRepository,
                                   MapService mapService,
                                   OccupyCombatAsyncTask occupyCombatAsyncTask
                                  ) {
        super(seaActivityRepo);
        this.battleWithdrawService = battleWithdrawService;
        this.seaActivityRepository = seaActivityRepository;
        this.occupyService = occupyService;
        this.mapService = mapService;
        this.occupyCombatAsyncTask = occupyCombatAsyncTask;
    }

    @Override
    @Transactional
    public void cleanUpOnWithdraw(UserBase element, SeaActivity activity) {
        super.cleanUpOnWithdraw(element, activity);
        MissionType missionType = activity.getActiveMoveSession().getMissionType();
        if (missionType.equals(MissionType.SCOUT)) {return;}
        battleWithdrawService.withdrawFromBattle(element, activity);
        if (occupyService.occupiedByAlliance(activity.getKosProfile(), element)) {
            occupyCombatAsyncTask.sendQueryInvaderForceNotification(element.getKosProfile().getUser().getId());
            if (seaActivityRepository.countStationsActivity(element.getId(), activity.getKosProfile().getId()) == 0) {
                mapService.changeElementStatusToPeace(element);
            }
        }
    }

}
