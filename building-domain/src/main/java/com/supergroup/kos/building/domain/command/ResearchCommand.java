package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.model.mining.ResearchBuilding;
import com.supergroup.kos.building.domain.model.point.Point;
import com.supergroup.kos.building.domain.model.technology.UserTechnology;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ResearchCommand {
    private final ResearchBuilding researchBuilding;
    private final UserTechnology   userTechnology;
    private final Point            point;
}
