package com.supergroup.kos.dto.building;

import com.supergroup.kos.dto.upgrade.UpgradeRequirement;
import com.supergroup.kos.dto.upgrade.UpgradeReward;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpgradeVaultBuildingInfoResponse {
    private Long               duration;
    private UpgradeReward      reward;
    private Long               level;
    private UpgradeRequirement requirement;
    private Double             protectPercent;
}
