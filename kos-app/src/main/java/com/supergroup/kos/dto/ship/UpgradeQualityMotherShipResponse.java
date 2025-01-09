package com.supergroup.kos.dto.ship;

import com.supergroup.kos.building.domain.constant.MotherShipQualityKey;
import com.supergroup.kos.dto.upgrade.UpgradeRequirement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpgradeQualityMotherShipResponse {
    private Long                 duration;
    private MotherShipQualityKey quality;
    private UpgradeRequirement   requirement;
    private Double               percentStat;
    private Long                 slotWeapon;

}
