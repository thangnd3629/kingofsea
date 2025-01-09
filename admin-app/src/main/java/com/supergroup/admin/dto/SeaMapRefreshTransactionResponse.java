package com.supergroup.admin.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SeaMapRefreshTransactionResponse {
    private Long          id;
    private Integer       totalElementDeleted;
    private Integer       totalElementAccordingBaseCreated;
    private Integer       totalElementAccordingZoneSeaCreated;
    private LocalDateTime timeRefresh;
}
