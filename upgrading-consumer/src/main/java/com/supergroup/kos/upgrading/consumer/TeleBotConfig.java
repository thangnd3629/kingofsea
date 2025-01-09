package com.supergroup.kos.upgrading.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.pengrad.telegrambot.TelegramBot;

@Configuration
@Profile("!local")
public class TeleBotConfig {

    @Value("${telegram.bot.token}")
    private String TELE_BOT_TOKEN;

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(TELE_BOT_TOKEN);
    }
}
