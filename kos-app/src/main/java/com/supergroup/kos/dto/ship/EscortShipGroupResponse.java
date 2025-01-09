package com.supergroup.kos.dto.ship;

import java.util.List;

import com.supergroup.kos.building.domain.constant.EscortShipGroupLevel;
import com.supergroup.kos.building.domain.constant.EscortShipGroupName;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EscortShipGroupResponse {

    private EscortShipGroupName               groupName;
    private String                            thumbnail;
    private EscortShipGroupLevel              quality;
    private Double                            percentStat;
    private List<EscortShipStatisticResponse> escortShips;
}
