package com.supergroup.admin.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImageName {
    QUEEN("queens"),
    RELIC("relics"),
    WEAPON("weapon"),
    WEAPON_SET("weapon_set");

    private String key;
}
