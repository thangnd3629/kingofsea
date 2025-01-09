package com.supergroup.kos.dto.building;

import com.supergroup.kos.dto.upgrade.UpgradeReward;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class QueenUpgradeReward extends UpgradeReward {
    private Long numberOfQueenCard;
}
