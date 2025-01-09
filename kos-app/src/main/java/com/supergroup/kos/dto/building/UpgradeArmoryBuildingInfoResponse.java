
package com.supergroup.kos.dto.building;

import com.supergroup.kos.dto.upgrade.UpgradeArmoryReward;
import com.supergroup.kos.dto.upgrade.UpgradeRequirement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpgradeArmoryBuildingInfoResponse {
    private Long                duration;
    private UpgradeArmoryReward reward;
    private Long                level;
    private UpgradeRequirement  requirement;
}
