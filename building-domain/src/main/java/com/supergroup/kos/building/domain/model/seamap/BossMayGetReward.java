package com.supergroup.kos.building.domain.model.seamap;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BossMayGetReward implements Serializable {
    private BossMayGetRewardItem weapon;
    private BossMayGetRewardItem item;
    private BossMayGetRewardItem relic;
}