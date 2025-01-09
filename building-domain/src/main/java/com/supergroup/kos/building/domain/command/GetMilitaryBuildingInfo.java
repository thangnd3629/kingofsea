package com.supergroup.kos.building.domain.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class GetMilitaryBuildingInfo {
    private final Long kosProfileId;

    private Boolean checkUnlockBuilding;
}
