package com.supergroup.kos.building.domain.command;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CreateWeaponSetCommand {
    private Long weaponSetModelId;
    private Long kosProfileId;
}
