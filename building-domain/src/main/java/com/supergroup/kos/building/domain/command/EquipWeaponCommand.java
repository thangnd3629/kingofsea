package com.supergroup.kos.building.domain.command;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EquipWeaponCommand {
    private Long kosProfileId;
    private Long motherShipId;
    private Long weaponId;
    private Boolean isEquipping;
    private Boolean isWeaponSet;

}
