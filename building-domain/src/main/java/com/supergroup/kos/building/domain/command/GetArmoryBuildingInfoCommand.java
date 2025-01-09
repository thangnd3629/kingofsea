package com.supergroup.kos.building.domain.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GetArmoryBuildingInfoCommand {
    private final Long kosProfileId;
}
