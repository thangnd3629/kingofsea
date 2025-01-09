package com.supergroup.kos.notification.domain.command;

import com.supergroup.notification.service.DataMessage;
import com.supergroup.notification.service.NotificationMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class SendMessageCommand {
    private final Long                loginSessionId;
    private final DataMessage         dataMessage;
    private final NotificationMessage notificationMessage;
}
