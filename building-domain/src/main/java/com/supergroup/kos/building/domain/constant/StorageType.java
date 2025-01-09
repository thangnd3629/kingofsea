package com.supergroup.kos.building.domain.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StorageType {
    WOOD("Wood"),
    STONE("Stone"),
    GOLD("Gold");

    private String storageName;
}
