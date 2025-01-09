package com.supergroup.kos.building.domain.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@RequiredArgsConstructor
@Accessors(chain = true)
public class GetScoutBuildingInfoCommand {
    private final Long kosProfileId;

    private Boolean checkUnlockBuilding;
}
