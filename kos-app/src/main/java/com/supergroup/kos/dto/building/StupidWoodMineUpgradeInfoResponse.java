package com.supergroup.kos.dto.building;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class StupidWoodMineUpgradeInfoResponse {
    private Long   level;
    private Long   costGold;
    private Long   timeUpgrade;
    private Long   gpGain;
    private Long   costStone;
    private Long   maxWorker;
    private Long   costWood;
    private Long   levelResearchBuilding;
    private Double speedWorker;
}