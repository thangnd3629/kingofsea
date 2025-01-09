package com.supergroup.kos.upgrading.consumer;

import java.util.HashMap;
import java.util.Objects;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supergroup.core.constant.MessageBrokerConstants;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.dto.battle.EndBattleEvent;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRepository;
import com.supergroup.kos.building.domain.service.battle.BattleLiberateService;
import com.supergroup.kos.building.domain.service.battle.BattleMiningService;
import com.supergroup.kos.building.domain.service.battle.BattlePvEService;
import com.supergroup.kos.building.domain.service.battle.BattlePvPService;
import com.supergroup.kos.building.domain.service.battle.ModeBattleHandler;
import com.supergroup.kos.building.domain.task.BattleTask;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BattleDelayQueueConfig {
    private final ObjectMapper          SERIALIZER;
    private final BattlePvPService      battlePvPService;
    private final BattleLiberateService battleLiberateService;
    private final BattlePvEService      battlePvEService;
    private final BattleMiningService   battleMiningService;
    private final BattleRepository      battleRepository;
    private final ModeBattleHandler     modeBattleHandler;

    @Bean(value = MessageBrokerConstants.BATTLE_EXCHANGE)
    CustomExchange battleExchange() {
        var args = new HashMap<String, Object>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(MessageBrokerConstants.BATTLE_EXCHANGE, "x-delayed-message", true, false, args);
    }

    @Bean(value = MessageBrokerConstants.BATTLE_QUEUE)
    public Queue battleQueue() {
        return QueueBuilder.durable(MessageBrokerConstants.BATTLE_QUEUE)
                           .withArgument("x-dead-letter-exchange", "")
                           .withArgument("x-dead-letter-routing-key", MessageBrokerConstants.BATTLE_DLQ)
                           .build();
    }

    @Bean
    Binding bindingBattleQueue(@Qualifier(value = MessageBrokerConstants.BATTLE_QUEUE) Queue queue,
                               @Qualifier(value = MessageBrokerConstants.BATTLE_EXCHANGE) CustomExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(MessageBrokerConstants.BATTLE_QUEUE)
                .noargs();
    }

    @Bean
    Queue battleDeadLetterQueue() {
        return QueueBuilder.durable(MessageBrokerConstants.BATTLE_DLQ).build();
    }

    @Bean(value = MessageBrokerConstants.BATTLE_END_EXCHANGE)
    DirectExchange battleEndExchange() {
        return new DirectExchange(MessageBrokerConstants.BATTLE_END_EXCHANGE);
    }

    @Bean(value = MessageBrokerConstants.BATTLE_END_QUEUE)
    public Queue battleEndQueue() {
        return QueueBuilder.durable(MessageBrokerConstants.BATTLE_END_QUEUE)
                           .build();
    }

    @Bean
    Binding bindingBattleEndQueue(@Qualifier(value = MessageBrokerConstants.BATTLE_END_QUEUE) Queue queue,
                                  @Qualifier(value = MessageBrokerConstants.BATTLE_END_EXCHANGE) DirectExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(MessageBrokerConstants.BATTLE_END_QUEUE);
    }

    @RabbitListener(queues = MessageBrokerConstants.BATTLE_END_QUEUE)
    @Transactional
    public void processBattleEndSignal(String input) throws Exception {
        try {
            EndBattleEvent event = SERIALIZER.readValue(input, EndBattleEvent.class);
            Battle battle = battleRepository.findById(event.getBattleId()).orElse(null);
            if (Objects.nonNull(battle)){
                modeBattleHandler.getMode(battle).onBattleEnded(battle, event.getWinner());
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!(e instanceof KOSException)) {
                Sentry.captureException(e);
                throw e;
            }
        }
    }

    @RabbitListener(queues = MessageBrokerConstants.BATTLE_QUEUE)
    @Transactional
    public void battleTask(String input) throws Exception {
        try {
            BattleTask message = SERIALIZER.readValue(input, BattleTask.class);
            log.info("Battle:{}:Task change status", message.getBattleId());
            switch (message.getBattleType()) {
                case OCCUPY:
                case ATTACK:
                    battlePvPService.changeStatusBattle(message);
                    break;
                case LIBERATE:
                    battleLiberateService.changeStatusBattle(message);
                    break;
                case MONSTER:
                    battlePvEService.changeStatusBattle(message);
                    break;
                case MINE:
                    battleMiningService.changeStatusBattle(message);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!(e instanceof KOSException)) {
                Sentry.captureException(e);
                throw e;
            }
        }
    }
}
