package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.constant.BuildingName;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetAllUpgradeInfoCommand {
    private final BuildingName name;
    private final Long         kosProfileId;
}
