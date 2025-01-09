package com.supergroup.kos.building.domain.model.battle;

import java.util.Map;

import com.supergroup.kos.building.domain.constant.EscortShipType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class LineUpRoundBattle {
    private Long                                  kosProfileId;
    private MotherShipBattle                      motherShip;
    private Map<EscortShipType, EscortShipBattle> escortShip;

}
