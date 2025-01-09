package com.supergroup.kos.dto.building;

import com.supergroup.kos.dto.upgrade.UpgradeRequirement;
import com.supergroup.kos.dto.upgrade.UpgradeReward;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ScoutUpgradeInfo {
    private Long               level;
    private Long               duration;
    private Long               capacity;
    private UpgradeReward      reward;
    private UpgradeRequirement requirement;
}
