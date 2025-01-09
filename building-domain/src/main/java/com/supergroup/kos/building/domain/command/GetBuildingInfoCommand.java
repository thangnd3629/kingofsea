package com.supergroup.kos.building.domain.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@RequiredArgsConstructor
@Accessors(chain = true)
public class GetBuildingInfoCommand {
    private final Long kosProfileId;

    private Boolean checkValidUnlock;
}
