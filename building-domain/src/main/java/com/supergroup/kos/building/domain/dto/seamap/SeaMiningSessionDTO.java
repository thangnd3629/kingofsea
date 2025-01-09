package com.supergroup.kos.building.domain.dto.seamap;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SeaMiningSessionDTO {
    private Long          id;
    private LocalDateTime timeStart;
    private Double        speed;
    private Double        collectedResource;
    private Double        remainingResource;
    private Long          activityId;
    private Long          lineUpId;

}
