package com.supergroup.kos.dto.ship;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipWeaponsRequest {
    private List<Long> weaponIds;
    private List<Long> weaponSetIds;
}
