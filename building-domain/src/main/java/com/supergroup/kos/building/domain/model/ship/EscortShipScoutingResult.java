package com.supergroup.kos.building.domain.model.ship;

import com.supergroup.kos.building.domain.constant.EscortShipGroupLevel;
import com.supergroup.kos.building.domain.constant.EscortShipGroupName;
import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.model.scout.ShipScoutingResult;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EscortShipScoutingResult extends ShipScoutingResult {
    private EscortShipGroupName  groupName;
    private Long                 amount;
    private EscortShipGroupLevel quality;
    private EscortShipType       type;

}
