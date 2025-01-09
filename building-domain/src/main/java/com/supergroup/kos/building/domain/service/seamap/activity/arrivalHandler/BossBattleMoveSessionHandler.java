package com.supergroup.kos.building.domain.service.seamap.activity.arrivalHandler;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.kos.building.domain.command.WithdrawActivityCommand;
import com.supergroup.kos.building.domain.constant.battle.BattleType;
import com.supergroup.kos.building.domain.constant.seamap.BossSeaStatus;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.seamap.BossSea;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaActivityRepository;
import com.supergroup.kos.building.domain.service.battle.BattlePvEService;
import com.supergroup.kos.building.domain.service.battle.InitBattleService;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BossBattleMoveSessionHandler extends ArrivalHandler<BossSea> {

    private final BattlePvEService   battlePvEService;
    private final SeaActivityService seaActivityService;
    private final InitBattleService  initBattleService;
    private final UserBaseService    userBaseService;
    private final BattleRepository   battleRepository;

    @Autowired
    public BossBattleMoveSessionHandler(SeaActivityRepository seaActivityRepository,
                                        BattleRepository battleRepository,
                                        BattlePvEService battlePvEService,
                                        SeaActivityService seaActivityService,
                                        InitBattleService initBattleService,
                                        UserBaseService userBaseService) {
        super(seaActivityRepository);
        this.battleRepository = battleRepository;
        this.battlePvEService = battlePvEService;
        this.seaActivityService = seaActivityService;
        this.initBattleService = initBattleService;
        this.userBaseService = userBaseService;

    }

    /**
     * Find boss and start boss battle
     */
    @Override
    @Transactional
    public void handleMove(BossSea element, MoveSession session, SeaActivity activity) {
        super.handleMove(element, session, activity);
        UserBase userBase = userBaseService.getByKosProfileId(activity.getKosProfile().getId());
        if (Objects.isNull(element.getBattle()) && element.getStatus().equals(BossSeaStatus.NORMAL)) {
            Battle battle = initBattleService.initBattle(BattleType.MONSTER, userBase, element, element.getCoordinates());
            // get full-battle from database
            Battle savedBattle = battleRepository.findById(battle.getId()).orElseThrow();
            battlePvEService.startBattle(savedBattle, activity, element);
        } else {
            seaActivityService.withdraw(new WithdrawActivityCommand().setId(activity.getId()));
        }
    }
}
