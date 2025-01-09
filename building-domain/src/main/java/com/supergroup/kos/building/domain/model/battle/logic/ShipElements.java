package com.supergroup.kos.building.domain.model.battle.logic;

import java.util.List;

import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.constant.battle.ShipType;
import com.supergroup.kos.building.domain.model.battle.BattleUnit;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ShipElements {
    private List<BattleUnit> ships;
    private ShipType         shipType;
    private EscortShipType   escortShipType;
}
