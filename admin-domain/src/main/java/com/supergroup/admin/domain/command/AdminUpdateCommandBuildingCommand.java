package com.supergroup.admin.domain.command;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AdminUpdateCommandBuildingCommand {
    @NotNull
    private Long kosProfileId;
    private Long maxSlotWeaponOfMotherShip;
    private Long level;
}
