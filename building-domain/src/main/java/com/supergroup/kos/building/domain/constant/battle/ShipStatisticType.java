package com.supergroup.kos.building.domain.constant.battle;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ShipStatisticType {
    ATK1("Physical attack"),
    DEF1("Armour"),
    ATK2("Fire power"),
    DEF2("Fire resistance"),
    CMD("Command"),
    SPEED("Speed"),
    HP("HP"),
    TONNAGE("Tonnage"),
    DODGE("Dodge");
    private final String statDisplayName;
}
