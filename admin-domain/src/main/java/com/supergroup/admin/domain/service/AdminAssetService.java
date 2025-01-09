package com.supergroup.admin.domain.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.supergroup.admin.domain.command.AdminUpdateAssetCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.repository.persistence.asset.AssetsRepository;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.CastleBuildingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminAssetService {

    private final AssetsRepository assetsRepository;
    private final AssetsService    assetsService;
    private final CastleBuildingService castleBuildingService;

    @Transactional
    public Assets update(Long kosProfileId, AdminUpdateAssetCommand command) {
        var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfileId));
        var castleBuilding = castleBuildingService.getCastleBuilding(new KosProfileCommand().setKosProfileId(kosProfileId));
        if (command.getWood() != null) {
            asset.setWood(command.getWood());
        }
        if (command.getStone() != null) {
            asset.setStone(command.getStone());
        }
        if (command.getGold() != null) {
            asset.setGold(command.getGold());
        }
        if (command.getPeople() != null) {
            var diff = command.getPeople() - asset.getTotalPeople();
            castleBuilding.setIdlePeople(castleBuilding.getIdlePeople() + diff);
            castleBuildingService.save(castleBuilding);
        }
        assetsRepository.save(asset);
        return assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfileId));
    }

}
