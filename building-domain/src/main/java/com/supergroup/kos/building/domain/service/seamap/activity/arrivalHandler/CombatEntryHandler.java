package com.supergroup.kos.building.domain.service.seamap.activity.arrivalHandler;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.supergroup.kos.building.domain.async.OccupyCombatAsyncTask;
import com.supergroup.kos.building.domain.command.WithdrawActivityCommand;
import com.supergroup.kos.building.domain.constant.battle.BattleType;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Invader;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.model.seamap.movesession.MissionType;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRepository;
import com.supergroup.kos.building.domain.service.battle.BattleLiberateService;
import com.supergroup.kos.building.domain.service.battle.BattlePvPService;
import com.supergroup.kos.building.domain.service.battle.InitBattleService;
import com.supergroup.kos.building.domain.service.battle.OccupyService;
import com.supergroup.kos.building.domain.service.seamap.AllianceService;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class CombatEntryHandler {
    private final InitBattleService     initBattleService;
    private final BattlePvPService      battlePvPService;
    private final SeaActivityService    seaActivityService;
    private final OccupyService         occupyService;
    private final UserBaseService       userBaseService;
    private final BattleRepository      battleRepository;
    private final BattleLiberateService battleLiberateService;
    private final OccupyCombatAsyncTask occupyCombatAsyncTask;
    private final AllianceService       allianceService;

    private BattleType getBattleType(MissionType missionType) {
        BattleType battleType = null;
        switch (missionType) {
            case OCCUPY:
                battleType = BattleType.OCCUPY;
                break;
            case ATTACK:
                battleType = BattleType.ATTACK;
                break;
        }
        return battleType;
    }

    public void handleUserBaseCombatMove(UserBase element, MoveSession session, SeaActivity activity) {
        KosProfile userKosProfile = activity.getKosProfile();
        MissionType missionType = session.getMissionType();
        Battle battleOnTarget = element.getBattle();
        Invader invader = element.getInvader();
        UserBase userBase = userBaseService.getByKosProfileId(userKosProfile.getId());
        if (Objects.isNull(battleOnTarget) && Objects.isNull(invader)) {
            if (Objects.isNull(session.getBattleId())) {
                BattleType battleType = getBattleType(missionType);
                if (Objects.isNull(battleType)) {
                    seaActivityService.withdraw(new WithdrawActivityCommand().setId(activity.getId()));
                    return;
                }
                Battle battle = initBattleService.initBattle(battleType, userBase, element, element.getCoordinates());
                // get full-battle from database
                Battle savedBattle = battleRepository.findById(battle.getId()).orElseThrow();
                battlePvPService.startBattle(savedBattle, activity, element);
            } else {
                seaActivityService.withdraw(new WithdrawActivityCommand().setId(activity.getId()));
            }

        } else if (Objects.isNull(battleOnTarget)) {
            if (missionType.equals(MissionType.ATTACK)) {
                seaActivityService.withdraw(new WithdrawActivityCommand().setId(activity.getId()));
                return;
            }
            if (occupyService.occupiedByAlliance(userKosProfile, element)) {
                seaActivityService.stationOnBase(activity, element);

            } else { // invaded by enemy
                if (missionType.equals(MissionType.STATION)) {
                    seaActivityService.withdraw(new WithdrawActivityCommand().setId(activity.getId()));
                } else {
                    // cancel battle when session's target is invader
                    if (Objects.nonNull(session.getKosTargetId()) && session.getKosTargetId().equals(invader.getKosProfileInvader().getId())) {
                        battleLiberateService.cancelBattle(element);
                        Battle battle = initBattleService.initBattle(BattleType.OCCUPY, userBase, invader.getKosProfileInvader().getBase(),
                                                                     element.getCoordinates());
                        // get full-battle from database
                        Battle savedBattle = battleRepository.findById(battle.getId()).orElseThrow();
                        battlePvPService.startBattle(savedBattle, activity, element);

                    } else {
                        seaActivityService.withdraw(new WithdrawActivityCommand().setId(activity.getId()));
                    }

                }
            }
        } else { // battle occurring on target
            if (battlePvPService.canJoinCombat(activity, element)) {
                BattleProfile attacker = battleOnTarget.getAttacker();
                BattleProfile defender = battleOnTarget.getDefender();
                if (allianceService.belongToSameAlliance(userKosProfile.getId(), attacker.getKosProfile().getId())) {
                    battlePvPService.joinBattle(battleOnTarget, attacker, activity);
                } else if (allianceService.belongToSameAlliance(userKosProfile.getId(), defender.getKosProfile().getId())) {
                    battlePvPService.joinBattle(battleOnTarget, defender, activity);
                } else {
                    seaActivityService.withdraw(new WithdrawActivityCommand().setId(activity.getId()));
                }
            } else {
                seaActivityService.withdraw(new WithdrawActivityCommand().setId(activity.getId()));
            }
        }
    }

}
