package com.supergroup.kos.dto.ship;

import com.supergroup.kos.building.domain.constant.MotherShipQualityKey;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MotherShipQualityConfigResponse {
    private MotherShipQualityKey quality;
    private Long                 slotWeapon;
    private Double               percentStat;
    private Long                 wood;
    private Long                 stone;
    private Long                 gold;
    private Long                 upgradeDuration;
}
