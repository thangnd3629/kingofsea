package com.supergroup.kos.dto.building;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpgradeInfoBase {
    private Long currentLevel;
    private Long nextLevel;
    private RequiredUpgradeBase required;
    private RewardUpgradeBase reward;
}
