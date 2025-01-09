package com.supergroup.kos.upgrading.consumer;

import java.util.HashMap;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supergroup.core.constant.MessageBrokerConstants;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.ItemServiceAsyncTask;
import com.supergroup.kos.building.domain.task.ExpiredItemTask;

import io.sentry.Sentry;

@Configuration
public class ExpiredItemConsumerConfig {

    @Autowired
    private ObjectMapper         objectMapper;
    @Autowired
    private ItemServiceAsyncTask itemServiceAsyncTask;

    @Bean
    CustomExchange expiredItemExchange() {
        var args = new HashMap<String, Object>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(MessageBrokerConstants.EXPIRED_ITEM_EXCHANGE, "x-delayed-message", true, false, args);
    }

    @Bean
    public Queue expiredItemQueue() {
        return QueueBuilder.durable(MessageBrokerConstants.EXPIRED_ITEM_QUEUE)
                           .withArgument("x-dead-letter-exchange", "")
                           .withArgument("x-dead-letter-routing-key", MessageBrokerConstants.EXPIRED_ITEM_DLQ)
                           .build();
    }

    @Bean
    Binding bindingExpiredItem(@Qualifier("expiredItemQueue") Queue queue,
                               @Qualifier("expiredItemExchange") CustomExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("expired-item")
                .noargs();
    }

    @Bean
    Queue expiredItemDeadLetterQueue() {
        return QueueBuilder.durable(MessageBrokerConstants.EXPIRED_ITEM_DLQ).build();
    }

    @RabbitListener(queues = MessageBrokerConstants.EXPIRED_ITEM_QUEUE)
    public void receiveExpiredItem(String input) throws JsonProcessingException {
        try {
            var task = objectMapper.readValue(input, ExpiredItemTask.class);
            itemServiceAsyncTask.sendExpiredItemNotification(task.getUseItemId());
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
