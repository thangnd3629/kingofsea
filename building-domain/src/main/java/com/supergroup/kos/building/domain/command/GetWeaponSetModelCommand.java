package com.supergroup.kos.building.domain.command;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class GetWeaponSetModelCommand {
    private Long kosProfileId;
    private Long weaponSetModelId;
}
