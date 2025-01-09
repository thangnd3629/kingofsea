package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.building.BaseBuilding;
import com.supergroup.kos.building.domain.model.profile.KosProfile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UpgradeBuildingCommand {
    private final KosProfile   kosProfile;
    private final BaseBuilding building;
    private final Assets       assets;
}