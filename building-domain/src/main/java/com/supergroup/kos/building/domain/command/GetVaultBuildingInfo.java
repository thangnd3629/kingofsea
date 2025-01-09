package com.supergroup.kos.building.domain.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@AllArgsConstructor
public class GetVaultBuildingInfo {
    private final Long kosProfileId;

    private Boolean validateBuilding = true;
}
