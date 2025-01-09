package com.supergroup.kos.dto.upgrade;

import java.util.List;

import com.supergroup.kos.dto.building.BuildingDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpgradeRequirement {
    private Long              gold;
    private Long              wood;
    private Long              stone;
    private List<BuildingDTO> buildings;
}
