package com.supergroup.kos.dto.ship;

import com.supergroup.kos.dto.upgrade.UpgradeEscortShipTypeRequirement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpgradeEscortShipResponse {
    private Long                             level;
    private Long                             duration;
    private UpgradeEscortShipTypeRequirement requirement;
    private Double                           percentStat;
}
