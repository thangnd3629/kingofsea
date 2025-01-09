package com.supergroup.kos.dto.building;

import com.supergroup.kos.dto.upgrade.UpgradeCommandBuildingReward;
import com.supergroup.kos.dto.upgrade.UpgradeRequirement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpgradeCommandBuildingInfoResponse {
    private Long                         duration;
    private UpgradeCommandBuildingReward reward;
    private Long                         level;
    private UpgradeRequirement           requirement;
    private Long                         slotMotherShip;
}
