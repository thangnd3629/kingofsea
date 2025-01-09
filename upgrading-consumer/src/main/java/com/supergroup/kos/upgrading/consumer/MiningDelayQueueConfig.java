package com.supergroup.kos.upgrading.consumer;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supergroup.core.constant.MessageBrokerConstants;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.SeaActivityAsyncTask;
import com.supergroup.kos.building.domain.command.WithdrawActivityCommand;
import com.supergroup.kos.building.domain.dto.seamap.SeaMiningSessionDTO;
import com.supergroup.kos.building.domain.model.seamap.SeaMiningSession;
import com.supergroup.kos.building.domain.service.seamap.activity.MoveSessionService;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;
import com.supergroup.kos.building.domain.service.seamap.mining.ResourceMiningService;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MiningDelayQueueConfig {
    private final ObjectMapper SERIALIZER;
    private final ResourceMiningService resourceMiningService;
    private final MoveSessionService moveSessionService;
    private final SeaActivityService seaActivityService;
    private final SeaActivityAsyncTask seaActivityAsyncTask;

    @Bean(value = MessageBrokerConstants.MINING_SESSION_EXCHANGE)
    CustomExchange miningExchange() {
        var args = new HashMap<String, Object>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(MessageBrokerConstants.MINING_SESSION_EXCHANGE, "x-delayed-message", true, false, args);
    }

    @Bean(value = MessageBrokerConstants.MINING_SESSION_QUEUE)
    public Queue miningQueue() {
        return QueueBuilder.durable(MessageBrokerConstants.MINING_SESSION_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", MessageBrokerConstants.MINING_SESSION_DLQ)
                .build();
    }

    @Bean
    Queue miningSessionDeadLetterQueue() {
        return QueueBuilder.durable(MessageBrokerConstants.MINING_SESSION_DLQ).build();
    }

    @Bean
    Binding bindingMiningQueue(@Qualifier(value = MessageBrokerConstants.MINING_SESSION_QUEUE) Queue queue,
                               @Qualifier(value = MessageBrokerConstants.MINING_SESSION_EXCHANGE) CustomExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(MessageBrokerConstants.MINING_SESSION_QUEUE)
                .noargs();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @RabbitListener(queues = MessageBrokerConstants.MINING_SESSION_QUEUE)
    public void onFinishMining(String input) throws Exception {
        try {
            SeaMiningSessionDTO message = SERIALIZER.readValue(input, SeaMiningSessionDTO.class);
            SeaMiningSession miningSession;
            Optional<SeaMiningSession> optional = resourceMiningService.findById(message.getId());
            if (optional.isEmpty()) {
                return;
            }
            miningSession = optional.get();
            if (miningSession.getIsDeleted()) {
                log.info("Mining session was invalidated");
                return;
            }
            seaActivityService.withdraw(new WithdrawActivityCommand().setId(miningSession.getSeaActivity().getId()));
            log.info("Successfully process mining session {}", miningSession.getId());
        } catch (Exception e) {
            if (e instanceof KOSException) {
                e.printStackTrace();
            } else {
                Sentry.captureException(e);
                throw e;
            }
        }
    }

}
