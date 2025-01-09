package com.supergroup.kos.dto.building;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CastleUpgradeInfoResponse {
    private Long currentLevel;
    private Long nextLevel;
    private Long currentPopulation;
    private Long currentMaxPopulation;
    private Long costWood;
    private Long costStone;
    private Long costGold;
    private Long upgradeDuration;
    private Long gpGain;
    private Long maxPopulation;
}
