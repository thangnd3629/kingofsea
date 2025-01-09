package com.supergroup.kos.upgrading.consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.supergroup.email.service.EmailSender;
import com.supergroup.email.service.SendgridEmailSender;

@Configuration
public class EmailSenderConfig {

    @Bean
    public EmailSender emailSender(SendgridProperties properties) {
        return new SendgridEmailSender(properties.getMasterEmail(), properties.getToken());
    }
}
