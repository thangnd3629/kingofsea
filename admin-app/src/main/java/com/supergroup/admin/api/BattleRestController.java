package com.supergroup.admin.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.admin.dto.SwitchStatusRequest;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.battle.BattleRound;
import com.supergroup.kos.building.domain.model.battle.BattleRoundSnapshot;
import com.supergroup.kos.building.domain.model.battle.BattleUnit;
import com.supergroup.kos.building.domain.model.battle.logic.BattleProgress;
import com.supergroup.kos.building.domain.model.battle.logic.BattleProgressPvEResult;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRepository;
import com.supergroup.kos.building.domain.service.battle.BattlePvEService;
import com.supergroup.kos.building.domain.service.battle.BattlePvPService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/battle")
@RequiredArgsConstructor
public class BattleRestController {
    private final BattlePvPService  battlePvPService;
    private final BattlePvEService  battlePvEService;
    private final KosProfileService kosProfileService;
    private final BattleRepository  battleRepository;

    @PostMapping("/logic-pvp")
    private ResponseEntity<BattleProgress> battleLogicPvP(@RequestBody BattleProgress battleProgress) {
        battlePvPService.battleProgress(battleProgress);
        return ResponseEntity.ok(battleProgress);
    }

    @PostMapping("/logic-pve")
    private ResponseEntity<List<BattleProgress>> battleLogicPvE(@RequestBody BattleProgress battleProgress) {
        BattleProgressPvEResult battleProgressPvEResult = battlePvEService.battleProgress(battleProgress);
        List<BattleProgress> battleProgresses = battleProgressPvEResult.getBattleProgresses();
        for(BattleProgress bp : battleProgresses){
            battlePvEService.staticAttack(bp.getAttacker());
            battlePvEService.staticAttack(bp.getDefender());
            updateBattleProgress(bp);
        }
        return ResponseEntity.ok(battleProgresses);
    }

    @PostMapping("/status")
    @Transactional
    private ResponseEntity<?> switchStatus(@RequestBody SwitchStatusRequest switchStatusRequest) {
        var kosProfile = kosProfileService.findById(switchStatusRequest.getKosProfileId()).orElseThrow(
                () -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND));
        var listBattle = battlePvPService.findDefendInProgressBattle(kosProfile);
        for (Battle battle : listBattle) {
            battle.setStatus(switchStatusRequest.getBattleStatus());
            if (battle.getStatus().equals(BattleStatus.END)) {
                battle.getBattleReport().setWinner(battle.getBattleReport().getInitiator());
                battle.getBattleReport().setLoser(battle.getBattleReport().getVictim());
                battle.getBattleReport().setEndAt(LocalDateTime.now());
            } else {
                battle.getBattleReport().setWinner(null);
                battle.getBattleReport().setLoser(null);
                battle.getBattleReport().setEndAt(null);
            }
            battleRepository.save(battle);
        }
        return ResponseEntity.ok().build();
    }

    @Transactional
    @GetMapping("/progress-detail/{id}")
    private ResponseEntity<?> getBattleProgressDetail(@PathVariable Long id) {
        List<BattleProgress> response = new ArrayList<>();
        Battle battle = battleRepository.findById(id).orElseThrow(() -> KOSException.of(ErrorCode.BAD_REQUEST_ERROR));
        List<BattleRound> battleRounds = battle.getBattleRounds();
        for (BattleRound battleRound : battleRounds) {
            BattleRoundSnapshot snapshot = battleRound.getBattleRoundSnapshot();
            if (Objects.nonNull(snapshot)) {
                response.add(new BattleProgress().setRound(snapshot.getCurrentRound()).setAttacker(snapshot.getAttackerModel())
                                                 .setDefender(snapshot.getDefenderModel()));
            }
        }
        return ResponseEntity.ok(response);
    }

    private void updateBattleProgress(BattleProgress battleProgress){
        updateBattleUnit(battleProgress.getAttacker().getBattleFields());
        updateBattleUnit(battleProgress.getDefender().getBattleFields());
    }

    private void updateBattleUnit(Map<String, List<BattleUnit>> battleFields) {
        for(var entry: battleFields.entrySet()){
            List<BattleUnit> battleUnits = entry.getValue();
            for(BattleUnit battleUnit:battleUnits) {
                Integer fighting = (int) Math.ceil((battleUnit.getHpLostAfterRound() + battleUnit.getCurrentHp()) / battleUnit.getHp().doubleValue());
                battleUnit.setFighting(fighting).setAmount(fighting);
            }
        }
    }
}
