package com.supergroup.kos.dto.weapon;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class WeaponSetMergeResponse {
    private Boolean isSuccess;
    private WeaponSetResponse weaponSet;
    private WeaponResponse weaponLost;
}
