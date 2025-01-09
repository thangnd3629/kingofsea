package com.supergroup.kos.dto.building;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CommandBuildingResponse {
    private Long level;
    private Long slotMotherShip;
    private Long numberOfMotherShip;

}
