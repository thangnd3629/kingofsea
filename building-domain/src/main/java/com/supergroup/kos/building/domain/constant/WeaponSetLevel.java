package com.supergroup.kos.building.domain.constant;

import java.util.Arrays;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WeaponSetLevel {
    COMMON(0),
    UNCOMMON(1),
    RARE(2),
    EPIC(3),
    LEGENDARY(4);

    private final Integer key;

    public static WeaponSetLevel of(Integer key) {
        return Arrays.stream(WeaponSetLevel.values()).filter(weaponSetLevel -> weaponSetLevel.key.equals(key))
                     .findFirst().orElseThrow(() -> KOSException.of(ErrorCode.WEAPON_SET_LEVEL_CONFIG_IS_NOT_FOUND));
    }

    public static WeaponSetLevel getNextLevel(WeaponSetLevel weaponSetLevel) {
        return WeaponSetLevel.of(weaponSetLevel.getKey() + 1);
    }

}
