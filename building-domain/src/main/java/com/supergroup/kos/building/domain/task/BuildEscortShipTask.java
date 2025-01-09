package com.supergroup.kos.building.domain.task;

import com.supergroup.kos.building.domain.constant.EscortShipType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BuildEscortShipTask {
    private Long           userId;
    private Long           kosId;
    private EscortShipType type;
    private Long           amount;
}
