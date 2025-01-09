package com.supergroup.kos.dto.ship;

import com.supergroup.kos.building.domain.constant.EscortShipGroupLevel;
import com.supergroup.kos.building.domain.constant.EscortShipGroupName;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EscortShipGroupLevelReward {
    private EscortShipGroupLevel quality;
    private EscortShipGroupName  shipGroup;
}
