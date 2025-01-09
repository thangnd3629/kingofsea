package com.supergroup.kos.upgrading.consumer;

import java.util.HashMap;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
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
import com.supergroup.kos.building.domain.dto.battle.RevivalBossMessage;
import com.supergroup.kos.building.domain.repository.persistence.seamap.BossSeaElementRepository;
import com.supergroup.kos.building.domain.service.seamap.MapService;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RevivalBossConsumer {

    private final ObjectMapper             SERIALIZER;
    private final MapService               mapService;
    private final BossSeaElementRepository bossSeaElementRepository;

    @Bean(value = MessageBrokerConstants.REVIVAL_BOSS_EXCHANGE)
    CustomExchange revivalBossExchange() {
        var args = new HashMap<String, Object>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(MessageBrokerConstants.REVIVAL_BOSS_EXCHANGE, "x-delayed-message", true, false, args);
    }

    @Bean(value = MessageBrokerConstants.REVIVAL_BOSS_QUEUE)
    public Queue revivalBossQueue() {
        return QueueBuilder.durable(MessageBrokerConstants.REVIVAL_BOSS_QUEUE)
                           .build();
    }

    @Bean
    Binding bindingRevivalBossQueue(@Qualifier(value = MessageBrokerConstants.REVIVAL_BOSS_QUEUE) Queue queue,
                                    @Qualifier(value = MessageBrokerConstants.REVIVAL_BOSS_EXCHANGE) CustomExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(MessageBrokerConstants.REVIVAL_BOSS_QUEUE)
                .noargs();
    }

    @RabbitListener(queues = MessageBrokerConstants.REVIVAL_BOSS_QUEUE)
    @Transactional
    public void reviveBoss(String input) throws Exception {
        try {
            var message = SERIALIZER.readValue(input, RevivalBossMessage.class);
            log.info("Revive boss {}", message.getId());
            var boss = bossSeaElementRepository.findById(message.getId());
            if (boss.isEmpty()) {return;}
            mapService.reviveBoss(boss.get());
        } catch (Exception e) {
            e.printStackTrace();
            if (!(e instanceof KOSException)) {
                Sentry.captureException(e);
                throw e;
            }
        }
    }
}
