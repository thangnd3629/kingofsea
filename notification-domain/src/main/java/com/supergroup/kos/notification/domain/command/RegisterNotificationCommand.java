package com.supergroup.kos.notification.domain.command;

import com.supergroup.auth.domain.model.LoginSession;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RegisterNotificationCommand {
    private final String       token;
    private final LoginSession loginSession;
}
