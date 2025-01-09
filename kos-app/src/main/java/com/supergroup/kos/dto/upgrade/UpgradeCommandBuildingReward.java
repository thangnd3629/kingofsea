package com.supergroup.kos.dto.upgrade;

import com.supergroup.kos.building.domain.constant.MotherShipQualityKey;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpgradeCommandBuildingReward extends UpgradeReward {
    private Long                 unLockMotherShipLevel;
    private MotherShipQualityKey unLockMotherShipQuality;
}
