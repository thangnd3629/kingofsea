package com.supergroup.kos.dto.ship;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.supergroup.kos.building.domain.constant.EscortShipType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuildEscortShipRequest {
    @NotNull
    private EscortShipType type;
    @NotNull
    @Min(1)
    private Long           amount;
}
