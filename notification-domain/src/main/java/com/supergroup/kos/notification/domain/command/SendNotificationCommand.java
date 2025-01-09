package com.supergroup.kos.notification.domain.command;

import com.supergroup.kos.notification.domain.constant.NotificationType;
import com.supergroup.kos.notification.domain.constant.SourceType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SendNotificationCommand {
    private final Object       kosMessage;
    private final String           title;
    private final String           body;
    private final String           data;
    private final NotificationType notificationType;
    private final SourceType       sourceType;
    private final Long             userId;
}
