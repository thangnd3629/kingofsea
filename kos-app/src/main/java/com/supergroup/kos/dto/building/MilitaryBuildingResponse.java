package com.supergroup.kos.dto.building;

import java.util.List;

import com.supergroup.kos.dto.ship.EscortShipGroupResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MilitaryBuildingResponse {
    private Long                          level;
    private List<EscortShipGroupResponse> escortShipGroups;

}
