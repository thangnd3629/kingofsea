package com.supergroup.kos.dto.ship;

import com.supergroup.kos.building.domain.constant.EscortShipGroupName;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EscortShipGroupConfigResponse {
    private Long                id;
    private EscortShipGroupName name;
    private String              description;
}
