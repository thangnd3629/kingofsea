package com.supergroup.kos.building.domain.model.upgrade;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpgradeInfo {
    private Long wood;
    private Long stone;
    private Long gold;
    private Long upgradeDuration;
    private Long gpPoint;
}
