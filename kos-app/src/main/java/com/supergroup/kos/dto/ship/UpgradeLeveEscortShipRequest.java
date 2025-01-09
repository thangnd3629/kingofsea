package com.supergroup.kos.dto.ship;

import com.supergroup.kos.building.domain.constant.EscortShipType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpgradeLeveEscortShipRequest {
    private EscortShipType type;
}
