package com.supergroup.kos.upgrading.consumer;

import static com.supergroup.core.constant.MessageBrokerConstants.NOTIFICATION_DLQ;
import static com.supergroup.core.constant.MessageBrokerConstants.NOTIFICATION_EXCHANGE;
import static com.supergroup.core.constant.MessageBrokerConstants.NOTIFICATION_QUEUE;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.supergroup.auth.domain.service.LoginSessionService;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.constant.MessageBrokerConstants;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.notification.domain.model.NotificationProducerPayload;
import com.supergroup.notification.service.FcmMessage;
import com.supergroup.notification.service.FcmTo;
import com.supergroup.notification.service.MessageSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class NotificationQueueConfig {
    private final ObjectMapper        SERIALIZER;
    private final MessageSender       messageSender;
    private final LoginSessionService loginSessionService;

    @Bean(value = NOTIFICATION_EXCHANGE)
    DirectExchange notificationExchange() {
        var args = new HashMap<String, Object>();
        args.put("x-delayed-type", "direct");
        return new DirectExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean(value = NOTIFICATION_QUEUE)
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE)
                           .withArgument("x-dead-letter-exchange", "")
                           .withArgument("x-dead-letter-routing-key", NOTIFICATION_DLQ)
                           .build();
    }

    @Bean(value = NOTIFICATION_DLQ)
    public Queue notificationDLQ() {
        return QueueBuilder.durable(NOTIFICATION_DLQ)
                           .build();
    }

    @Bean
    Binding bindingNotificationQueue(@Qualifier(value = MessageBrokerConstants.NOTIFICATION_QUEUE) Queue queue,
                                     @Qualifier(value = MessageBrokerConstants.NOTIFICATION_EXCHANGE) DirectExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(MessageBrokerConstants.NOTIFICATION_QUEUE);
    }

    @RabbitListener(queues = NOTIFICATION_QUEUE)
    public void processNotificationMessage(String input) {
        try {
            NotificationProducerPayload payload = SERIALIZER.readValue(input, NotificationProducerPayload.class);
            List<Long> userIds = payload.getUserIds();
            if (userIds.size() == 1) {
                Long userId = userIds.get(0);
                var session = loginSessionService.findFirstByUser_IdOrderByUpdatedAtDesc(userId)
                                                 .orElseThrow(() -> KOSException.of(ErrorCode.USER_IS_LOGGED_OUT));
                if (Objects.isNull(session.getFcmToken())) {
                    log.info("No fcm found for user {}", session.getUser().getId());
                    return;
                }
                FcmMessage fcmMessage = new FcmMessage(new FcmTo(session.getFcmToken()),
                                                       payload.getDataMessage(),
                                                       payload.getNotificationMessage());
                messageSender.send(fcmMessage);
            }

            //TODO implement fcm broadcast

        } catch (JsonProcessingException e) {
            throw KOSException.of(ErrorCode.MESSAGE_QUEUE_DATA_TYPE_MISMATCH);
        } catch (FirebaseMessagingException e) {
            log.info("Can not send message to firebase server");
            // ignore message
        }

    }

}
