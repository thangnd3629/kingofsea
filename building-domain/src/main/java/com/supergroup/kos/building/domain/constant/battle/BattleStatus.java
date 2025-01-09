package com.supergroup.kos.building.domain.constant.battle;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BattleStatus {
    INIT,
    PROGRESS,
    BREAK,
    END,
    CANCEL,
    ATTACKER_WITHDRAWAL;

    public static List<BattleStatus> getStatusIgnore() {
        return List.of(END, CANCEL);
    }
    public static List<BattleStatus> getStatusActive() {
        return List.of(INIT, PROGRESS, BREAK);
    }
}
