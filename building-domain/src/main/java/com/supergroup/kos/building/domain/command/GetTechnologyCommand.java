package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.constant.TechnologyCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GetTechnologyCommand {
    private final Long           kosProfileId;
    private final TechnologyCode code;
}
