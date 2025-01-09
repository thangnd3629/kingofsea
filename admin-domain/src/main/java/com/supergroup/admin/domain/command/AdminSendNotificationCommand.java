package com.supergroup.admin.domain.command;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminSendNotificationCommand {
    private Long   userId;
    private String title;
    private String body; // html
    private String data; // metadata
    private String sourceType;
    private String notificationType;
}
