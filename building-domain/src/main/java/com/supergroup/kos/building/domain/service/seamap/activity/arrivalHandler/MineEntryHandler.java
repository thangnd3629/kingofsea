package com.supergroup.kos.building.domain.service.seamap.activity.arrivalHandler;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.supergroup.kos.building.domain.command.WithdrawActivityCommand;
import com.supergroup.kos.building.domain.constant.battle.BattleType;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.scout.Scout;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.ResourceIsland;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaMiningSession;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.model.seamap.movesession.MissionType;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaActivityRepository;
import com.supergroup.kos.building.domain.service.battle.BattleMiningService;
import com.supergroup.kos.building.domain.service.battle.InitBattleService;
import com.supergroup.kos.building.domain.service.scout.ScoutMode;
import com.supergroup.kos.building.domain.service.scout.ScoutModeFactory;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;
import com.supergroup.kos.building.domain.service.seamap.mining.ResourceMiningService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MineEntryHandler extends ArrivalHandler<ResourceIsland> {
    private final ResourceMiningService resourceMiningService;
    private final SeaActivityService    seaActivityService;
    private final BattleMiningService   battleMiningService;
    private final InitBattleService     initBattleService;
    private final UserBaseService       userBaseService;
    private final BattleRepository      battleRepository;
    private final ScoutModeFactory      scoutModeFactory;

    @Autowired
    public MineEntryHandler(SeaActivityRepository seaActivityRepository,
                            ResourceMiningService resourceMiningService,
                            SeaActivityService seaActivityService,
                            BattleMiningService battleMiningService,
                            InitBattleService initBattleService,
                            UserBaseService userBaseService,
                            BattleRepository battleRepository,
                            ScoutModeFactory scoutModeFactory) {
        super(seaActivityRepository);
        this.resourceMiningService = resourceMiningService;
        this.seaActivityService = seaActivityService;
        this.battleMiningService = battleMiningService;
        this.initBattleService = initBattleService;
        this.userBaseService = userBaseService;
        this.battleRepository = battleRepository;
        this.scoutModeFactory = scoutModeFactory;
    }

    @Override
    public void handleMove(ResourceIsland resourceIsland, MoveSession session, SeaActivity activity) {
        super.handleMove(resourceIsland, session, activity);
        MissionType missionType = session.getMissionType();
        switch (missionType) {
            case MINING:
            case ATTACK:
                if (Objects.nonNull(resourceIsland.getBattle())) {
                    seaActivityService.withdraw(new WithdrawActivityCommand().setId(activity.getId()));
                    return;
                }
                if (Objects.nonNull(resourceIsland.getMiningSession())) {
                    SeaMiningSession miningSession = resourceIsland.getMiningSession();
                    KosProfile minerKosProfile = miningSession.getSeaActivity().getKosProfile();
                    if (minerKosProfile.getId().equals(activity.getKosProfile().getId())) {
                        seaActivityService.withdraw(new WithdrawActivityCommand().setId(activity.getId()));
                        return;
                    }
                    if (Objects.isNull(session.getKosTargetId()) || !session.getKosTargetId().equals(minerKosProfile.getId())) {
                        seaActivityService.withdraw(new WithdrawActivityCommand().setId(activity.getId()));
                        return;
                    }
                    if (miningSession.getSeaActivity().getKosProfile().getId().equals(activity.getKosProfile().getId())) {
                        seaActivityService.withdraw(new WithdrawActivityCommand().setId(activity.getId()));
                        return;
                    }
                    UserBase userbase = userBaseService.getByKosProfileId(activity.getKosProfile().getId());
                    SeaActivity defender = resourceIsland.getMiningSession().getSeaActivity();
                    Battle battle = initBattleService.initBattle(BattleType.MINE, userbase, resourceIsland, session.getEnd());
                    // get full-battle from database
                    Battle savedBattle = battleRepository.findById(battle.getId()).orElseThrow();
                    battleMiningService.startBattle(savedBattle, resourceIsland, activity, defender);
                    resourceMiningService.pauseSession(miningSession);
                    return;
                }
                resourceMiningService.startMiningSession(activity, resourceIsland);
                seaActivityService.save(activity);
                return;
            case SCOUT:
                Scout scout = activity.getScout();
                ScoutMode scoutMode = scoutModeFactory.getMode(scout);
                if (Objects.nonNull(scoutMode)) {
                    scoutMode.scoutInEnemyPlace(scout);
                }
                seaActivityService.withdraw(new WithdrawActivityCommand().setId(activity.getId()));
                return;
        }

    }
}
