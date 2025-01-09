package com.supergroup.kos.notification.domain.model;

import java.util.List;

import com.supergroup.notification.service.DataMessage;
import com.supergroup.notification.service.NotificationMessage;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NotificationProducerPayload {
    private List<Long> userIds;
    private NotificationMessage notificationMessage;
    private DataMessage dataMessage;
}
