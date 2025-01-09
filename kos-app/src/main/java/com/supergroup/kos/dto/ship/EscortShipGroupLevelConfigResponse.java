
package com.supergroup.kos.dto.ship;

import com.supergroup.kos.building.domain.constant.EscortShipGroupLevel;
import com.supergroup.kos.building.domain.constant.EscortShipGroupName;
import com.supergroup.kos.dto.upgrade.UpgradeRequirement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EscortShipGroupLevelConfigResponse {
    private EscortShipGroupName  shipGroup;
    private EscortShipGroupLevel quality;
    private Double               percentStat;
    private UpgradeRequirement   requirement;
}
