package com.supergroup.kos.building.domain.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum BuildingConfigKey {
    CASTLE_CONFIG("CASTLE_CONFIG"),
    WOOD_MINE_CONFIG("WOOD_MINE_CONFIG");

    String name;
}