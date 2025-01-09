package com.supergroup.kos.dto.building;

import java.util.List;

import com.supergroup.kos.dto.ship.EscortShipNameResponse;
import com.supergroup.kos.dto.upgrade.UpgradeReward;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MilitaryUpgradeReward extends UpgradeReward {
    private List<EscortShipNameResponse> unLockEscortShips;
}
