package com.supergroup.kos.upgrading.consumer;

import static com.supergroup.core.constant.MessageBrokerConstants.MOVE_SESSION_STATUS_CHANGE_DLQ;
import static com.supergroup.core.constant.MessageBrokerConstants.MOVE_SESSION_STATUS_CHANGE_EXCHANGE;
import static com.supergroup.core.constant.MessageBrokerConstants.MOVE_SESSION_STATUS_CHANGE_QUEUE;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.SeaActivityAsyncTask;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.dto.movesession.MoveSessionChangeMessage;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaActivityRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MoveSessionStatusChangeConsumer {

    private final ObjectMapper                     SERIALIZER;
    private final SeaElementRepository<SeaElement> seaElementRepository;
    private final SeaActivityRepository            seaActivityRepository;
    private final SeaActivityAsyncTask             seaActivityAsyncTask;

    @Bean
    DirectExchange moveSessionStatusChangeExchange() {
        var args = new HashMap<String, Object>();
        args.put("x-delayed-type", "direct");
        return new DirectExchange(MOVE_SESSION_STATUS_CHANGE_EXCHANGE);
    }

    @Bean
    public Queue moveSessionStatusChangeQueue() {
        return QueueBuilder.durable(MOVE_SESSION_STATUS_CHANGE_QUEUE)
                           .withArgument("x-dead-letter-exchange", "")
                           .withArgument("x-dead-letter-routing-key", MOVE_SESSION_STATUS_CHANGE_DLQ)
                           .build();
    }

    @Bean
    public Queue moveSessionStatusChangeDLQ() {
        return QueueBuilder.durable(MOVE_SESSION_STATUS_CHANGE_DLQ)
                           .build();
    }

    @Bean
    Binding bingMoveSessionQueue(@Qualifier(value = "moveSessionStatusChangeQueue") Queue queue,
                                 @Qualifier(value = "moveSessionStatusChangeExchange") DirectExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(MOVE_SESSION_STATUS_CHANGE_QUEUE);
    }

    @RabbitListener(queues = MOVE_SESSION_STATUS_CHANGE_QUEUE)
    @Transactional
    public void processMessage(String message) throws JsonProcessingException {
        try {
            log.info("Process message {}", message);
            var moveSessionChangeMessage = SERIALIZER.readValue(message, MoveSessionChangeMessage.class);
            var sourceId = moveSessionChangeMessage.getSourceId();
            var destinationId = moveSessionChangeMessage.getDestinationId();
            SeaElement source = null;
            SeaElement destination = null;
            if (Objects.nonNull(sourceId)){
                source = seaElementRepository.findById(moveSessionChangeMessage.getSourceId())
                        .orElse(null);
            }
            if (Objects.nonNull(destinationId)){
                destination = seaElementRepository.findById(moveSessionChangeMessage.getDestinationId())
                        .orElse(null);
            }

            var activity = seaActivityRepository.findById(moveSessionChangeMessage.getSeaActivityId())
                                                .orElse(null);

            if (Objects.isNull(activity)) {
                log.info("Activity is null. Ignore message");
                return;
            }

            if (Objects.nonNull(source)) {
                seaActivityAsyncTask.sendQueryBattleStatusNotification(activity.getKosProfile().getUser().getId());
            }
            if (Objects.nonNull(destination)) {
                List<SeaActivity> activities = seaActivityRepository.findByStationAt(destination.getId());

                Set<Long> userIds = activities.stream().map(a -> a.getKosProfile()
                                                                  .getUser()
                                                                  .getId())
                                              .collect(Collectors.toSet());
                if (destination.type().equals(SeaElementType.USER_BASE)) {
                    userIds.add(((UserBase) destination).getKosProfile().getUser().getId());
                }
                for (Long userId : userIds) {
                    seaActivityAsyncTask.sendQueryBattleStatusNotification(userId);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            if (exception instanceof KOSException) {
                // ignore
            } else {
                throw exception;
            }
        }
    }
}
