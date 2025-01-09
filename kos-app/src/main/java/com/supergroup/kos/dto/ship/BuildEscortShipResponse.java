package com.supergroup.kos.dto.ship;

import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.dto.upgrade.UpgradeRequirement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BuildEscortShipResponse {
    private EscortShipType     type;
    private String             name;
    private Long               duration;
    private UpgradeRequirement requirement;
}
