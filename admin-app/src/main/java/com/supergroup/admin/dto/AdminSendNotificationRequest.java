package com.supergroup.admin.dto;

import com.supergroup.kos.notification.domain.dto.NotificationDTO;
import com.supergroup.notification.service.DataMessage;
import com.supergroup.notification.service.NotificationMessage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminSendNotificationRequest {
    private Long                userId;
    private DataMessage         dataMessagePayload;
    private NotificationMessage notificationMessagePayload;
    private NotificationDTO     persistentPayload;
}