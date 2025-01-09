package com.supergroup.kos.dto.ship;

import java.util.List;

import com.supergroup.kos.dto.building.BuildingDTO;
import com.supergroup.kos.dto.technology.TechnologyDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BuyShipRequirement {
    private Long                  gold;
    private List<BuildingDTO> buildings;
    private TechnologyDTO     technology;
}
