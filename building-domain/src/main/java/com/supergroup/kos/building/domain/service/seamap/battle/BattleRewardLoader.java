package com.supergroup.kos.building.domain.service.seamap.battle;

import org.springframework.stereotype.Component;

import com.supergroup.kos.building.domain.model.battle.BattleReward;
import com.supergroup.kos.building.domain.model.seamap.reward.LoadedOnShipReward;
import com.supergroup.kos.building.domain.service.seamap.RewardLoader;

@Component
public class BattleRewardLoader implements RewardLoader<BattleReward> {

    @Override
    public LoadedOnShipReward loadOnShip(BattleReward reward) {
        return new LoadedOnShipReward().setWood(reward.getWood())
                                       .setGold(reward.getGold())
                                       .setStone(reward.getStone())
                                       .setRelics(reward.getRelics())
                                       .setQueens(reward.getQueens())
                                       .setItems(reward.getItems())
                                       .setWeapons(reward.getWeapons());
    }
}
