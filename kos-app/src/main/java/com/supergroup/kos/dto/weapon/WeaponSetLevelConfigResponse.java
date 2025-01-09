package com.supergroup.kos.dto.weapon;

import com.supergroup.kos.building.domain.constant.WeaponSetLevel;
import com.supergroup.kos.dto.upgrade.UpgradeRequirement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class WeaponSetLevelConfigResponse {
    private WeaponSetLevel     quality;
    private UpgradeRequirement requirement;
    private Double             percentStat;
}
