package com.supergroup.kos.notification.domain.command;

import java.util.List;

import com.supergroup.kos.notification.domain.constant.SourceType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SendMultiNotificationCommand {
    private String     title;
    private String     body;
    private List<Long> userIds;
    private String     content;
    private SourceType sourceType;
}
