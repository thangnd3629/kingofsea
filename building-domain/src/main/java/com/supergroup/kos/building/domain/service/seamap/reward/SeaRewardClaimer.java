package com.supergroup.kos.building.domain.service.seamap.reward;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.supergroup.kos.building.domain.async.AssetsServiceAsyncTask;
import com.supergroup.kos.building.domain.command.GetStorageBuildingCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.constant.StorageType;
import com.supergroup.kos.building.domain.model.building.StorageBuilding;
import com.supergroup.kos.building.domain.model.item.Item;
import com.supergroup.kos.building.domain.model.item.UserItem;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.queen.Queen;
import com.supergroup.kos.building.domain.model.relic.Relic;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.reward.LoadedOnShipReward;
import com.supergroup.kos.building.domain.model.weapon.Weapon;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.StorageBuildingService;
import com.supergroup.kos.building.domain.service.queen.QueenService;
import com.supergroup.kos.building.domain.service.relic.RelicService;
import com.supergroup.kos.building.domain.service.seamap.item.UserItemService;
import com.supergroup.kos.building.domain.service.weapon.WeaponService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeaRewardClaimer {
    private final StorageBuildingService storageBuildingService;
    private final AssetsServiceAsyncTask assetsServiceAsyncTask;
    private final AssetsService          assetsService;
    private final WeaponService          weaponService;
    private final QueenService           queenService;
    private final RelicService           relicService;
    private final UserItemService        userItemService;

    private void claimWood(SeaActivity activity, LoadedOnShipReward totalReward) {
        KosProfile kosProfile = activity.getKosProfile();
        var assets = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
        if (totalReward.getWood() > 0) {
            StorageBuilding storageBuilding = storageBuildingService.getBuilding(
                    new GetStorageBuildingCommand(StorageType.WOOD, kosProfile.getId()).setCheckValidUnlock(false));
            Long capacity = storageBuilding.getCapacity();
            Double collectableResource = totalReward.getWood();
            if (storageBuilding.getAmount() + collectableResource > capacity) {
                assetsServiceAsyncTask.sendFullCapNotification(storageBuilding.getStorageType(), kosProfile.getUser().getId());
                assets.setWood(Double.valueOf(capacity));
            } else {
                assets.setWood(assets.getWood() + collectableResource);
            }
        }
    }

    private void claimStone(SeaActivity activity, LoadedOnShipReward totalReward) {
        KosProfile kosProfile = activity.getKosProfile();
        var assets = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
        if (totalReward.getStone() > 0) {
            StorageBuilding storageBuilding = storageBuildingService.getBuilding(
                    new GetStorageBuildingCommand(StorageType.STONE, kosProfile.getId()).setCheckValidUnlock(false));
            Long capacity = storageBuilding.getCapacity();
            Double collectableResource = totalReward.getStone();
            if (storageBuilding.getAmount() + collectableResource > capacity) {
                assetsServiceAsyncTask.sendFullCapNotification(storageBuilding.getStorageType(), kosProfile.getUser().getId());
                assets.setStone(Double.valueOf(capacity));
            } else {
                assets.setStone(assets.getStone() + collectableResource);
            }
        }
        assetsService.save(assets);
    }

    private void claimGold(SeaActivity activity, LoadedOnShipReward totalReward) {
        KosProfile kosProfile = activity.getKosProfile();
        var assets = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
        if (totalReward.getGold() > 0) {
            StorageBuilding storageBuilding = storageBuildingService.getBuilding(
                    new GetStorageBuildingCommand(StorageType.GOLD, kosProfile.getId()).setCheckValidUnlock(false));
            Long capacity = storageBuilding.getCapacity();
            Double collectableResource = totalReward.getGold();
            if (storageBuilding.getAmount() + collectableResource > capacity) {
                assetsServiceAsyncTask.sendFullCapNotification(storageBuilding.getStorageType(), kosProfile.getUser().getId());
                assets.setGold(Double.valueOf(capacity));
            } else {
                assets.setGold(assets.getGold() + collectableResource);
            }
        }
        assetsService.save(assets);
    }

    private void claimRelics(SeaActivity activity, LoadedOnShipReward totalReward) {
        for (Relic relic : totalReward.getRelics()) {
            relic.setCommunityBuilding(activity.getKosProfile().getCommunityBuilding());
        }
        relicService.saveAll(totalReward.getRelics());
    }

    private void claimQueens(SeaActivity activity, LoadedOnShipReward totalReward) {
        for (Queen queen : totalReward.getQueens()) {
            queen.setQueenBuilding(activity.getKosProfile().getQueenBuilding());
        }
        queenService.saveAll(totalReward.getQueens());
    }

    private void claimWeapon(SeaActivity activity, LoadedOnShipReward totalReward) {
        for (Weapon weapon : totalReward.getWeapons()) {
            weapon.setAssets(activity.getKosProfile().getAssets());
        }
        weaponService.saveAll(totalReward.getWeapons());
    }

    private void claimItem(SeaActivity activity, LoadedOnShipReward totalReward) {
        for (Item item : totalReward.getItems()) {
            var userItem = new UserItem().setItem(item)
                                         .setIsUsed(false)
                                         .setAsset(activity.getKosProfile().getAssets());
            userItemService.save(userItem);
        }
    }

    public void claimRewardOnBaseArrival(SeaActivity activity, LoadedOnShipReward totalReward) {
        totalReward.setUnPaidTax(new ArrayList<>());
        claimGold(activity, totalReward);
        claimWood(activity, totalReward);
        claimStone(activity, totalReward);
        claimWeapon(activity, totalReward);
        claimRelics(activity, totalReward);
        claimQueens(activity, totalReward);
        claimItem(activity, totalReward);
    }

}
