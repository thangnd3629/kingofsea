package com.supergroup.kos.notification.domain.service;

import static com.supergroup.core.constant.MessageBrokerConstants.NOTIFICATION_EXCHANGE;
import static com.supergroup.core.constant.MessageBrokerConstants.NOTIFICATION_QUEUE;

import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supergroup.kos.notification.domain.model.NotificationProducerPayload;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationProducer {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper   SERIALIZER;

    public void sendToQueue(NotificationProducerPayload message) throws JsonProcessingException {
        String payload = SERIALIZER.writeValueAsString(message);
        var prop = new MessageProperties();
        var mess = MessageBuilder.withBody(payload.getBytes())
                                 .andProperties(prop)
                                 .build();
        rabbitTemplate.convertAndSend(NOTIFICATION_EXCHANGE, NOTIFICATION_QUEUE, mess);
    }
}
