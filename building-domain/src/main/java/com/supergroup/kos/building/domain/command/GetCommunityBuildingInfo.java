package com.supergroup.kos.building.domain.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetCommunityBuildingInfo {
    private final Long kosProfileId;
}
