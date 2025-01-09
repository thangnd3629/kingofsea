package com.supergroup.kos.building.domain.service.seamap.item;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetStorageBuildingCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.constant.StorageType;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.building.StorageBuilding;
import com.supergroup.kos.building.domain.model.item.Item;
import com.supergroup.kos.building.domain.model.item.ItemEffect;
import com.supergroup.kos.building.domain.model.item.UseItemResult;
import com.supergroup.kos.building.domain.model.item.UserItem;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.StorageBuildingService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResourceItemHandler implements ItemHandler {

    /**
     * This method handle all resource item
     */

    private final AssetsService          assetsService;
    private final StorageBuildingService storageBuildingService;

    @Override
    public UseItemResult applyItem(UserItem userItem, ApplyItemCommand command) {
        var item = userItem.getItem();
        var asset = assetsService.findById(userItem.getAsset().getId()).orElseThrow(() -> KOSException.of(ErrorCode.KOS_ASSETS_NOT_FOUND));
        var kosProfileId = asset.getKosProfile().getId();
        switch (item.getNamespace()) {
            // STONE
            case STONE:
                increaseResource(kosProfileId, StorageType.STONE, item);
                break;

            // WOOD
            case WOOD:
                increaseResource(kosProfileId, StorageType.WOOD, item);
                break;
            // GOLD
            case GOLD:
                increaseResource(kosProfileId, StorageType.GOLD, item);
                break;
            default:
                throw KOSException.of(ErrorCode.NAMESPACE_ITEM_NOT_FOUND);
        }
        return null; // TODO should return meaningful value
    }

    @Override
    public void deactivateItem(UserItem item) {

    }

    private Double calculateValue(Double valueRoot, ItemEffect itemEffect) {
        switch (itemEffect.getTypeParameter()) {
            case FLAT:
                return valueRoot + Double.valueOf(itemEffect.getParameter());
            case PERCENT:
                return valueRoot * Double.valueOf(itemEffect.getParameter());
            default:
                throw KOSException.of(ErrorCode.TYPE_PARAMETER_ITEM_EFFECT_NOT_FOUND);
        }
    }

    private void increaseResource(Long kosProfileId, StorageType storageType, Item item) {
        Assets assets = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfileId));
        StorageBuilding storageBuilding = storageBuildingService.getBuilding(
                new GetStorageBuildingCommand(storageType, kosProfileId));
        Double currentResource = 0D;
        switch (storageType) {
            case WOOD:
                currentResource = assets.getWood();
                break;
            case STONE:
                currentResource = assets.getStone();
                break;
            case GOLD:
                currentResource = assets.getGold();
                break;
            default:
                throw KOSException.of(ErrorCode.STORAGE_TYPE_NOT_FOUND);
        }
        for (ItemEffect itemEffect : item.getEffects()) {
            currentResource = calculateValue(currentResource, itemEffect);
        }
        currentResource = currentResource < storageBuilding.getCapacity() ? currentResource : storageBuilding.getCapacity();
        switch (storageType) {
            case WOOD:
                assets.setWood(currentResource);
                break;
            case STONE:
                assets.setStone(currentResource);
                break;
            case GOLD:
                assets.setGold(currentResource);
                break;
            default:
                throw KOSException.of(ErrorCode.STORAGE_TYPE_NOT_FOUND);
        }
        assetsService.save(assets);
    }
}
