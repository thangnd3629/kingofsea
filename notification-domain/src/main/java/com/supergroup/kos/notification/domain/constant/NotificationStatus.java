package com.supergroup.kos.notification.domain.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationStatus {
    UNSEEN, SEEN, HIDE, DELETE;
}
