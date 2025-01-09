package com.supergroup.kos.dto.ship;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EscortShipResponse {
    private Long                     amount;
    private Long                     level;
    private Double                   percentStat;
    private EscortShipConfigResponse model;
    private EscortShipGroupInfoResponse  shipGroup;
}

