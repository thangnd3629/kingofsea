package com.supergroup.kos.building.domain.model.config.battle;

import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.constant.battle.ShipType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleFieldElementDamageConfig {
    private ShipType       shipType;
    private EscortShipType escortType;
    private Double         percentDamageTaken;

}
