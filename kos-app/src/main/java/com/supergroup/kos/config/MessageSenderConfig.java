package com.supergroup.kos.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.supergroup.notification.service.FcmMessageSender;
import com.supergroup.notification.service.MessageSender;

@Configuration
public class MessageSenderConfig {

    @Bean
    public MessageSender messageSender() throws IOException {
        var options = FirebaseOptions.builder()
                                     .setCredentials(GoogleCredentials.getApplicationDefault())
                                     .build();

        var app = FirebaseApp.initializeApp(options);
        return new FcmMessageSender(FirebaseMessaging.getInstance(app));
    }
}
