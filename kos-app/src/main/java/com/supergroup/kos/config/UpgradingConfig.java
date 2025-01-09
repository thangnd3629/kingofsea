package com.supergroup.kos.config;

import java.util.HashMap;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpgradingConfig {
    @Bean
    CustomExchange upgradingExchange() {
        var args = new HashMap<String, Object>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange("upgrading-exchange", "x-delayed-message", true, false, args);
    }

    @Bean
    public Queue upgradingQueue() {
        return new Queue("upgrading");
    }

    @Bean
    public Binding upgradingBinding(Queue q, Exchange e) {
        return BindingBuilder
                .bind(q)
                .to(e)
                .with("upgrading")
                .noargs();
    }
}
