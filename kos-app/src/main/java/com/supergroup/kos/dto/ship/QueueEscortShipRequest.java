package com.supergroup.kos.dto.ship;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.supergroup.kos.building.domain.constant.EscortShipType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueueEscortShipRequest {
    @NotNull
    private EscortShipType type;
    @Min(1)
    private Long           amount;
    @NotNull
    private Boolean        isQueueing;
}
