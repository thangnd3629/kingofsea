package com.supergroup.kos.notification.domain.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationDTO {
    private String                 title;
    private String                 body;
    private List<RenderContentDTO> rewards;
    private String                 actions;
}
