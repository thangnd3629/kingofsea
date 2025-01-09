package com.supergroup.kos.building.domain.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UpgradeMessageIntent {

    UPGRADE_BUILDING_DONE("UPGRADE:BUILDING:DONE");

    private String intent;
}
