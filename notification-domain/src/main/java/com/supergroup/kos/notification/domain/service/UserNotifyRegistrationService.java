package com.supergroup.kos.notification.domain.service;

import org.springframework.stereotype.Service;

import com.supergroup.auth.domain.repository.persistence.LoginSessionRepository;
import com.supergroup.kos.notification.domain.command.RegisterNotificationCommand;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserNotifyRegistrationService {

    private final LoginSessionRepository loginSessionRepository;

    /**
     * Register notification by user and fcm token
     */
    public void registerNotification(RegisterNotificationCommand command) {
        command.getLoginSession().setFcmToken(command.getToken());
        loginSessionRepository.save(command.getLoginSession());
    }

}
