package com.supergroup.kos.building.domain.constant.battle;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BattleType {
    ATTACK("Attack"),
    OCCUPY("Occupy"),
    LIBERATE("Liberate"),
    DEFEND("Defend"),
    MONSTER("Monster battle"),
    MINE("Mine combat");
    private final String displayName;
}
