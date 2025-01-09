package com.supergroup.kos.building.domain.model.battle;

import com.supergroup.kos.building.domain.constant.MotherShipQualityKey;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MotherShipBattle extends BattleUnit {
    private Long                 motherShipId; // motherShipID
    private MotherShipQualityKey quality;
    private Long                 hpAfterBattle;
    private String               owner;

//    private Integer getKilled() {
//        return this.getCurrentHp() <= 0 ? 1 : 0;
//    }
}
