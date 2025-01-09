package com.supergroup.kos.dto.notification;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CountNotificationUnseen {
    private Long count ;
}
