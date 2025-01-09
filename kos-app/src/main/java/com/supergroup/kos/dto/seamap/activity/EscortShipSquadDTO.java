package com.supergroup.kos.dto.seamap.activity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.supergroup.kos.dto.ship.EscortShipResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class EscortShipSquadDTO {
    private Long               amount;
    private EscortShipResponse escortShip;
}
