package com.supergroup.kos.dto.building;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class StupidStorageUpgradeInfoResponse {
    private Long levelHeadquarter;
    private Long timeUpgrade;
    private Long level;
    private Long gpGain;
    private Long costStone;
    private Long costWood;
    private Long costGold;
    private Long capacity;
}