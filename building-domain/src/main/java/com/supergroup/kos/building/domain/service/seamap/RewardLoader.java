package com.supergroup.kos.building.domain.service.seamap;

import com.supergroup.kos.building.domain.model.seamap.reward.LoadedOnShipReward;
import com.supergroup.kos.building.domain.model.seamap.reward.SeaReward;

public interface RewardLoader<T extends SeaReward> {
    LoadedOnShipReward loadOnShip(T reward);
}
