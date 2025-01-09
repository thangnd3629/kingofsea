package com.supergroup.kos.dto.upgrade;

import com.supergroup.kos.building.domain.constant.WeaponSetLevel;
import com.supergroup.kos.dto.ship.EscortShipGroupLevelReward;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpgradeArmoryReward extends UpgradeReward {
    private WeaponSetLevel             unLockWeaponSetLevel;
    private EscortShipGroupLevelReward unLockEscortShipGroupLevel;
}
