package com.supergroup.kos.dto.ship;

import javax.validation.constraints.NotNull;

import com.supergroup.kos.building.domain.constant.EscortShipType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelQueueEscortShipRequest {
    @NotNull
    private EscortShipType type;
}
