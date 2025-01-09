package com.supergroup.kos.building.domain.model.battle.logic;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleFieldRows {
    private List<ShipElements> shipList;
}
