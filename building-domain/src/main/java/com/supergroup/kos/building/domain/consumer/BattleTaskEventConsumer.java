package com.supergroup.kos.building.domain.consumer;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.supergroup.kos.building.domain.dto.battle.CreateTaskBattleEvent;
import com.supergroup.kos.building.domain.dto.battle.EndBattleEvent;
import com.supergroup.kos.building.domain.dto.battle.RevivalBossEvent;
import com.supergroup.kos.building.domain.service.battle.BattleLiberateService;
import com.supergroup.kos.building.domain.service.battle.BattleMiningService;
import com.supergroup.kos.building.domain.service.battle.BattlePvEService;
import com.supergroup.kos.building.domain.service.battle.BattlePvPService;
import com.supergroup.kos.building.domain.service.battle.ModeBattleHandler;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BattleTaskEventConsumer {

    private final BattlePvPService      battlePvPService;
    private final BattleLiberateService battleLiberateService;
    private final BattlePvEService      battlePvEService;
    private final BattleMiningService   battleMiningService;
    private final ModeBattleHandler     modeBattleHandler;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCreateBattleEvent(CreateTaskBattleEvent event) throws JsonProcessingException {
        switch (event.getBattle().getBattleType()){
            case ATTACK:
            case OCCUPY:
                battlePvPService.sendBattleTaskToQueue(event.getBattle());
                break;
            case LIBERATE:
                battleLiberateService.sendBattleTaskToQueue(event.getBattle());
                break;
            case MONSTER:
                battlePvEService.sendBattleTaskToQueue(event.getBattle());
                break;
            case MINE:
                battleMiningService.sendBattleTaskToQueue(event.getBattle());
                break;
            default:
                break;
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRevivalBossEvent(RevivalBossEvent event) throws JsonProcessingException {
        battlePvEService.sendRevivalTaskToQueue(event.getBossSea());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEndBattleEvent(EndBattleEvent event) throws JsonProcessingException {
        modeBattleHandler.sendBattleEndSignalToQueue(event);
    }
}
