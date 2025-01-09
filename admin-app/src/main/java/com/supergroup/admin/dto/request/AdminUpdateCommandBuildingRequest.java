package com.supergroup.admin.dto.request;

import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AdminUpdateCommandBuildingRequest {
    private Long level;
    @Min(0)
    private Long maxSlotWeaponOfMotherShip;
}
