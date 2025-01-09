package com.supergroup.kos.building.domain.service.seamap.activity;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.model.battle.BattleReward;
import com.supergroup.kos.building.domain.model.item.Item;
import com.supergroup.kos.building.domain.model.queen.Queen;
import com.supergroup.kos.building.domain.model.relic.Relic;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.reward.LoadedOnShipReward;
import com.supergroup.kos.building.domain.model.seamap.reward.MiningReward;
import com.supergroup.kos.building.domain.model.seamap.reward.SeaReward;
import com.supergroup.kos.building.domain.model.weapon.Weapon;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaActivityRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaRewardRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityRewardService {
    private final SeaActivityRepository seaActivityRepository;
    private final SeaRewardRepository   seaRewardRepository;

    public void loadOnShip(SeaActivity activity, SeaReward reward) { // on way back to colonies
        if (Objects.isNull(reward)) {return;}
        if (Objects.isNull(activity.getLoadedOnShipReward())) {return;}
        seaRewardRepository.save(reward);
        LoadedOnShipReward loadedOnShipReward = activity.getLoadedOnShipReward();
        if (reward instanceof MiningReward) {
            MiningReward miningReward = (MiningReward) reward;
            loadWood(loadedOnShipReward, miningReward.getWood());
            loadStone(loadedOnShipReward, miningReward.getStone());
        }
        if (reward instanceof BattleReward) {
            BattleReward battleReward = (BattleReward) reward;
            battleReward.setGoldRemaining(battleReward.getGoldRemaining() - loadGold(loadedOnShipReward, battleReward.getGoldRemaining()));
            battleReward.setWoodRemaining(battleReward.getWoodRemaining() - loadWood(loadedOnShipReward, battleReward.getWoodRemaining()));
            battleReward.setStoneRemaining(battleReward.getStoneRemaining() - loadStone(loadedOnShipReward, battleReward.getStoneRemaining()));

            if (!battleReward.getIsQueenLoaded()) {
                loadQueens(loadedOnShipReward, battleReward);
            }
            if (!battleReward.getIsRelicsLoaded()) {
                loadRelics(loadedOnShipReward, battleReward);
            }
            if (!battleReward.getIsWeaponLoaded()) {
                loadWeapons(loadedOnShipReward, battleReward);
            }
            if (!battleReward.getIsItemLoaded()) {
                loadItems(loadedOnShipReward, battleReward);
            }
        }
        seaActivityRepository.save(activity);
    }

    private boolean isTonnageExceeded(double remainingTonnage, double plus) {
        if (remainingTonnage < plus) {
            return true;
        }
        return false;
    }

    private Double loadStone(LoadedOnShipReward loadedOnShip, Double stone) {
        Double remainingTonnage = loadedOnShip.getRemainingTonnage();
        Double loadMore;
        if (isTonnageExceeded(remainingTonnage, stone)) {
            loadMore = remainingTonnage;
        } else {
            loadMore = stone;
        }
        loadedOnShip.setStone(loadedOnShip.getStone() + loadMore);

        return loadMore;
    }

    private Double loadWood(LoadedOnShipReward loadedOnShip, Double wood) {
        Double remainingTonnage = loadedOnShip.getRemainingTonnage();
        Double loadMore;
        if (isTonnageExceeded(remainingTonnage, wood)) {
            loadMore = remainingTonnage;
        } else {
            loadMore = wood;
        }
        loadedOnShip.setWood(loadedOnShip.getWood() + loadMore);

        return loadMore;
    }

    private Double loadGold(LoadedOnShipReward loadedOnShip, Double gold) {
        Double remainingTonnage = loadedOnShip.getRemainingTonnage();
        Double loadMore;
        if (isTonnageExceeded(remainingTonnage, gold)) {
            loadMore = remainingTonnage;
        } else {
            loadMore = gold;
        }
        loadedOnShip.setGold(loadedOnShip.getGold() + loadMore);

        return loadMore;
    }

    private void loadQueens(LoadedOnShipReward loadedOnShip, BattleReward battleReward) {
        List<Queen> resourceOnShip = loadedOnShip.getQueens();
        resourceOnShip.addAll(battleReward.getQueens());
        battleReward.setIsQueenLoaded(true);
    }

    private void loadRelics(LoadedOnShipReward loadedOnShip, BattleReward battleReward) {
        List<Relic> resourceOnShip = loadedOnShip.getRelics();
        resourceOnShip.addAll(battleReward.getRelics());
        battleReward.setIsRelicsLoaded(true);

    }

    private void loadWeapons(LoadedOnShipReward loadedOnShip, BattleReward battleReward) {
        List<Weapon> resourceOnShip = loadedOnShip.getWeapons();
        resourceOnShip.addAll(battleReward.getWeapons());
        battleReward.setIsRelicsLoaded(true);

    }

    private void loadItems(LoadedOnShipReward loadedOnShip, BattleReward battleReward) {
        List<Item> resourceOnShip = loadedOnShip.getItems();
        resourceOnShip.addAll(battleReward.getItems());
        battleReward.setIsRelicsLoaded(true);
    }

}
