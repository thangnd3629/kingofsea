package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.model.mining.ResearchBuilding;
import com.supergroup.kos.building.domain.model.point.Point;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ConvertGP2TPCommand {
    private final Point            point;
    private final ResearchBuilding researchBuilding;
    private final Long             amount;
}
