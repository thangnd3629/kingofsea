package com.supergroup.kos.building.domain.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author idev
 */
@Getter
@AllArgsConstructor
public enum BuildingName {
    // 0
    RESEARCH("Research"),
    // 1
    CASTLE("Castle"),
    // 2
    WOOD_MINE("Wood Mine"),
    // 3
    STONE_MINE("Stone Mine"),
    // 4
    STORAGE_WOOD("Wood Storage"),
    // 5
    STORAGE_STONE("Stone Storage"),
    // 6
    STORAGE_GOLD("Gold Storage"),
    // 7
    VAULT("Vault"),
    // 8
    QUEEN("Queen Palace"),
    // 9
    COMMUNITY("Community"),
    // 10
    ARMORY("Armory"),
    // 11
    MILITARY("Military"),
    //12
    SCOUT("Scout Department"),
    // 13
    COMMAND("Command Port"),
    // 14
    LIGHTHOUSE("Lighthouse");

    private String buildingName;
}
