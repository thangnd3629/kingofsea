package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.constant.EscortShipType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class GetEscortShipCommand {
    private Long                kosProfileId;
    private EscortShipType      shipType;
}
