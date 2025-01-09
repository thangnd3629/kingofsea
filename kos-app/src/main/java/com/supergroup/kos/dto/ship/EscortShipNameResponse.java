package com.supergroup.kos.dto.ship;

import com.supergroup.kos.building.domain.constant.EscortShipType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EscortShipNameResponse {
    private EscortShipType type;
    private String         name;
}
