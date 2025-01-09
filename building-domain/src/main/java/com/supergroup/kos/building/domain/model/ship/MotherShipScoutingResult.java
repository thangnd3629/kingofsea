package com.supergroup.kos.building.domain.model.ship;

import com.supergroup.kos.building.domain.constant.MotherShipQualityKey;
import com.supergroup.kos.building.domain.constant.MotherShipTypeKey;
import com.supergroup.kos.building.domain.model.scout.ShipScoutingResult;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MotherShipScoutingResult extends ShipScoutingResult {
    private Long                 cmd;
    private Long                 tng;
    private Long                 speed;
    private MotherShipTypeKey    type;
    private MotherShipQualityKey quality;
}
