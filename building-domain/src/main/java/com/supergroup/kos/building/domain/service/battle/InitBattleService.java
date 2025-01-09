package com.supergroup.kos.building.domain.service.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.constant.battle.BattleType;
import com.supergroup.kos.building.domain.constant.battle.FactionType;
import com.supergroup.kos.building.domain.constant.battle.FlatCheckResultBattle;
import com.supergroup.kos.building.domain.constant.seamap.SeaActivityStatus;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;
import com.supergroup.kos.building.domain.model.battle.BattleReport;
import com.supergroup.kos.building.domain.model.battle.CheckResultBattle;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.BossSea;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.ResourceIsland;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaActivityRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class InitBattleService {
    private final BattleRepository       battleRepository;
    private final BattleReportRepository battleReportRepository;
    private final BattleProfileService   battleProfileService;
    private final SeaActivityRepository  seaActivityRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Battle initBattle(BattleType type, SeaElement attacker, SeaElement defender, Coordinates battleSite) {
        log.info("Create battle. Type {} - Coordinates {}", type, battleSite);
        // create battle basic info
        Battle battle = new Battle();
        battle.setBattleSite(battleSite)
              .setBattleType(type)
              .setStatus(BattleStatus.INIT)
              .setCurrentRound(0L)
              .setCheckResult(new CheckResultBattle().setAttackerCheckResult(FlatCheckResultBattle.ALL)
                                                     .setDefenderCheckResult(FlatCheckResultBattle.ALL));
        battleRepository.save(battle);

        // create battle report
        BattleReport battleReport = new BattleReport().setCoordinates(battleSite)
                                                      .setBattle(battle);
        battleReport = battleReportRepository.save(battleReport);

        battle.setBattleReport(battleReport);

        // create attacker battle profile
        BattleProfile attackerProfile = battleProfileService.createUserBattleProfile(((UserBase) attacker).getKosProfile(),
                                                                                     battle,
                                                                                     FactionType.ATTACKER);
        // create defender battle profile
        BattleProfile defenderProfile = createBattleProfileForDefender(defender, battle, attacker);
        // set attacker and defender
        battle.setAttacker(attackerProfile)
              .setDefender(defenderProfile);
        battleReport.setInitiator(attackerProfile)
                    .setVictim(defenderProfile);
        // save to db
        battleReportRepository.save(battleReport);
        battle = battleRepository.save(battle);
        return battle;
    }

    private BattleProfile createBattleProfileForDefender(SeaElement defender, Battle battle, SeaElement battleField) {
        switch (battle.getBattleType()) {
            case ATTACK:
            case OCCUPY:
                return battleProfileService.createUserBattleProfile(((UserBase) defender).getKosProfile(),
                                                                    battle,
                                                                    FactionType.DEFENDER);
            case LIBERATE:
                UserBase userBase = (UserBase) defender;
                List<SeaActivity> seaActivities = seaActivityRepository.findByStationAtAndStatus(battleField.getId(), SeaActivityStatus.OCCUPYING);
                seaActivities = seaActivities.stream().filter(s -> SeaActivityStatus.OCCUPYING.equals(s.getStatus())).collect(Collectors.toList());
                if (seaActivities.isEmpty()) {
                    throw KOSException.of(ErrorCode.BAD_REQUEST_ERROR);
                }
                var kosProfiles = seaActivities.stream()
                                               .filter(s -> !s.getKosProfile().getId().equals(userBase.getKosProfile().getId()))
                                               .map(SeaActivity::getKosProfile).collect(Collectors.toSet());
                List<BattleProfile> battleProfiles = new ArrayList<>();
                for (KosProfile kosProfile : kosProfiles) {
                    battleProfiles.add(battleProfileService.createUserBattleProfile(kosProfile, battle, FactionType.DEFENDER));
                }
                BattleProfile battleProfile = battleProfileService.createUserBattleProfile(userBase.getKosProfile(), battle,
                                                                                           FactionType.DEFENDER);
                battleProfiles.add(battleProfile);
                battleProfileService.saveAll(battleProfiles);
                return battleProfile;
            case MINE:
                var resourceIsland = (ResourceIsland) defender;
                return battleProfileService.createUserBattleProfile(resourceIsland.getMiningSession().getSeaActivity().getKosProfile(),
                                                                    battle,
                                                                    FactionType.DEFENDER);
            case MONSTER:
                return battleProfileService.createBossBattleProfile((BossSea) defender,
                                                                    battle,
                                                                    FactionType.DEFENDER);
            default:
                throw new NotImplementedException("Battle type is coming soon...");
        }
    }

}
