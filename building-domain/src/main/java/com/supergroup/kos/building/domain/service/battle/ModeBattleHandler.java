package com.supergroup.kos.building.domain.service.battle;

import static com.supergroup.core.constant.MessageBrokerConstants.BATTLE_END_EXCHANGE;
import static com.supergroup.core.constant.MessageBrokerConstants.BATTLE_END_QUEUE;

import java.util.Objects;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.battle.BattleType;
import com.supergroup.kos.building.domain.dto.battle.EndBattleEvent;
import com.supergroup.kos.building.domain.model.battle.Battle;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ModeBattleHandler {
    private final BattlePvPService      battlePvPService;
    private final BattlePvEService      battlePvEService;
    private final BattleMiningService   battleMiningService;
    private final BattleLiberateService battleLiberateService;
    private final RabbitTemplate        rabbitTemplate;
    private final ObjectMapper          SERIALIZER;

    public BattleHandler getMode(Battle battle) {
        BattleType battleType = battle.getBattleType();
        if (Objects.isNull(battleType)) {
            throw KOSException.of(ErrorCode.BATTLE_TYPE_NOT_FOUND);
        }
        switch (battleType) {
            case ATTACK:
            case OCCUPY:
                return battlePvPService;
            case MONSTER:
                return battlePvEService;
            case MINE:
                return battleMiningService;
            case LIBERATE:
                return battleLiberateService;
            default:
                throw KOSException.of(ErrorCode.BATTLE_TYPE_NOT_FOUND);
        }
    }

    public void sendBattleEndSignalToQueue(EndBattleEvent event) {
        try {
            String task = SERIALIZER.writeValueAsString(event);
            rabbitTemplate.convertAndSend(BATTLE_END_EXCHANGE, BATTLE_END_QUEUE, task);
        } catch (Exception ignored) {}

    }
}
