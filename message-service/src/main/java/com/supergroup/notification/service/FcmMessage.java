package com.supergroup.notification.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class FcmMessage {
    private final To                  to;
    private final DataMessage         dataMessage;
    private final NotificationMessage notificationMessage;

}
