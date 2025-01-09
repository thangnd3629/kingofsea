package com.supergroup.kos.building.domain.task;

import com.supergroup.kos.building.domain.constant.UpgradeMotherShipType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpgradeMotherShipTask {
    private Long                  userId;
    private Long                  kosId;
    private Long                  motherShipId;
    private UpgradeMotherShipType type;
}
