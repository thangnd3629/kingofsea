package com.supergroup.kos.building.domain.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GetResearchBuildingInfo {
    private final Long kosProfileId;
}
