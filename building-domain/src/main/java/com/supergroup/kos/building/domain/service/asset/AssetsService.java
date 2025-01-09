package com.supergroup.kos.building.domain.service.asset;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.AssetsServiceAsyncTask;
import com.supergroup.kos.building.domain.command.GetStorageBuildingCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.UpdateGoldCommand;
import com.supergroup.kos.building.domain.constant.StorageType;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.repository.persistence.asset.AssetsRepository;
import com.supergroup.kos.building.domain.service.building.StorageBuildingService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Service
@RequiredArgsConstructor
public class AssetsService {
    @Delegate
    private final AssetsRepository       kosAssetsRepository;
    private final StorageBuildingService storageBuildingService;
    private final AssetsServiceAsyncTask assetsServiceAsyncTask;

    /**
     * Get asset info
     */
    public Assets getAssets(KosProfileCommand command) {
        Assets assets = kosAssetsRepository.findByKosProfile_Id(command.getKosProfileId())
                                           .orElseThrow(() -> KOSException.of(ErrorCode.KOS_ASSETS_NOT_FOUND));
        assets.setTotalPeople(getTotalPeople(command.getKosProfileId()).getTotalPeople());
        return assets;
    }

    /**
     * Save asset to database
     */
    @Transactional
    public void save(Assets assets) {
        var kosProfileId = assets.getKosProfile().getId();
        var woodStorage = storageBuildingService.getBuilding(
                new GetStorageBuildingCommand(StorageType.WOOD, kosProfileId).setCheckValidUnlock(false));
        var stoneStorage = storageBuildingService.getBuilding(
                new GetStorageBuildingCommand(StorageType.STONE, kosProfileId).setCheckValidUnlock(false));
        var goldStorage = storageBuildingService.getBuilding(
                new GetStorageBuildingCommand(StorageType.GOLD, kosProfileId).setCheckValidUnlock(false));
        if (assets.getWood() > woodStorage.getCapacity()) {
            assets.setWood(Double.valueOf(woodStorage.getCapacity()));
        }
        if (assets.getStone() > stoneStorage.getCapacity()) {
            assets.setStone(Double.valueOf(stoneStorage.getCapacity()));
        }
        if (assets.getGold() > goldStorage.getCapacity()) {
            assets.setGold(Double.valueOf(goldStorage.getCapacity()));
        }
        kosAssetsRepository.save(assets);
    }

    public void updateGold(UpdateGoldCommand command) {
        var assets = getAssets(new KosProfileCommand().setKosProfileId(command.getKosProfileId()));
        var assetsUpdated = assets.setGold(assets.getGold() + command.getDiffGold());
        // TODO logic for mining
        save(assetsUpdated);
    }
}
