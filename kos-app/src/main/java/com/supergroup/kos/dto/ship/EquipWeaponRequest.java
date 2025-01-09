package com.supergroup.kos.dto.ship;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipWeaponRequest {
    @NotNull
    private Boolean isEquipping;
    @NotNull
    private Boolean isWeaponSet;
}
