package com.supergroup.kos.building.domain.model.config.battle;

import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.constant.battle.ShipType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleFiledElementConfig {
    private Integer        quantity;
    private Double         percentDamageTaken = 0.1D;
    private ShipType       shipType;
    private EscortShipType escortShipType;
}
