package com.supergroup.kos.dto;

import com.supergroup.kos.dto.building.BuildingDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RequirementDTO {
    private Long        gold;
    private Long        wood;
    private Long        stone;
    private Long        levelBuilding;
    private Long        techPoint;
    private BuildingDTO building;
}