package com.supergroup.kos.dto.ship;

import com.supergroup.kos.building.domain.constant.EscortShipGroupName;
import com.supergroup.kos.building.domain.constant.EscortShipType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BuildShipStatusResponse {
    private Long                buildSessionId;
    private Long                amount;
    private Long                duration; // millis
    private Long                current; // millis
    private EscortShipType      type;
    private EscortShipGroupName groupName;
    private Boolean             isBuilding;
}
