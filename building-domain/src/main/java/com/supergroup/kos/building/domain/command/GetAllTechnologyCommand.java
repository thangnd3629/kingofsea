package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.constant.TechnologyType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GetAllTechnologyCommand {
    private final Long           kosProfileId;
    private final TechnologyType technologyType;
}
