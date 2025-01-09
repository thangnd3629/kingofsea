package com.supergroup.kos.building.domain.model.battle;

import java.util.List;

import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ShipLineUpBattle {
    private List<ShipLineUp> shipLineupAttacker;
    private List<ShipLineUp> shipLineupDefender;
}
