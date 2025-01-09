package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.constant.EscortShipType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BuildEscortShipCommand {
    private Long           kosProfileId;
    private EscortShipType type;
    private Long           amount;
    private Boolean        isCharged;
    private Long           userId;
}