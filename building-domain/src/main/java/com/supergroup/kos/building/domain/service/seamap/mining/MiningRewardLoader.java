package com.supergroup.kos.building.domain.service.seamap.mining;

import org.springframework.stereotype.Component;

import com.supergroup.kos.building.domain.model.seamap.reward.LoadedOnShipReward;
import com.supergroup.kos.building.domain.model.seamap.reward.MiningReward;
import com.supergroup.kos.building.domain.service.seamap.RewardLoader;

@Component
public class MiningRewardLoader implements RewardLoader<MiningReward> {
    @Override
    public LoadedOnShipReward loadOnShip(MiningReward reward) {
        return new LoadedOnShipReward().setWood(reward.getWood()).setStone(reward.getStone());
    }
}
