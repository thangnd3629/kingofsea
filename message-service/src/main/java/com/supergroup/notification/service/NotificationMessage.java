package com.supergroup.notification.service;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class NotificationMessage {
    private String title;
    private String body;

    public Boolean isEmpty() {
        return Objects.isNull(title)
               || title.isEmpty()
               || Objects.isNull(body)
               || body.isEmpty();
    }
}
