package com.supergroup.kos.building.domain.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EscortShipType {
    // Melee
    RAMMER_SHIP(0, "Rammer Ship", 1),
    CHARGER_SHIP(1, "Charger Ship", 2),
    LINE_BROKER(2, "Line Broker", 3),
    // Ranger
    GUNDALOW(3, "Gundalow", 1),
    GUNBOAT(4, "Gunboat", 2),
    SHALLOP(5, "Shallop", 3),
    // Heavy
    BRIG(6, "Brig", 1),
    AGGRESSOR(7, "Aggressor", 2),
    BASILISK(8, "Basilisk", 3);

    private final Integer key;
    private final String  escortShipTypeName;
    private final Integer indexInCombat;
}

