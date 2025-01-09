package com.supergroup.kos.dto.notification;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.supergroup.kos.notification.domain.constant.NotificationStatus;
import com.supergroup.kos.notification.domain.constant.NotificationType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class NotificationDTO {
    private Long               id;
    private String             title;
    private String             body;
    private NotificationStatus status;
    private NotificationType   notificationType;
    private List<Object>       renderContents;
    private List<Object>       actions;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime      createdAt;
}
