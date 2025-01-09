package com.supergroup.kos.dto.building;

import com.supergroup.kos.dto.RequirementDTO;
import com.supergroup.kos.dto.upgrade.UpgradeReward;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class StorageUpgradeInfoResponse {
    private Long          duration;
    private UpgradeReward  reward;
    private RequirementDTO requirement;
    private Long           capacity;
}