package com.supergroup.kos.dto.building;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class StupidStoneMineUpgradeInfoResponse {
    private Long costGold;
    private Long timeUpgrade;
    private Long level;
    private Long gpGain;
    private Long costStone;
    private Long maxWorker;
    private Long costWood;
    private Long levelResearchBuilding;
    private Long speedWorker;
}