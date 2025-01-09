package com.supergroup.kos.building.domain.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MissionResult {
    SUCCESS, FAIL, BETRAYED, COUNTER_SCOUT, NOT_FOUND_ENEMY_BASE, MISSION_TYPE_NOT_VALID, CANCEL;
}
