package com.supergroup.kos.building.domain.model.battle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.supergroup.kos.building.domain.constant.EscortShipGroupName;
import com.supergroup.kos.building.domain.constant.EscortShipType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EscortShipBattle extends BattleUnit {
    private Long                escortShipSquadId; // = EscortShipSquadId;
    private EscortShipType      escortShipType;
    private Long                hpLost;
    private EscortShipGroupName groupName;

    @JsonIgnore
    public Long getHpDebt() {
        Integer shipLiveAfterBattle = this.getCurrentHp() == 0 ? 0 : (int) Math.ceil(this.getCurrentHp() / this.getHp().doubleValue());
//        Integer shipLiveAfterBattle = (int) Math.ceil(this.getCurrentHp() / this.getHp().doubleValue());
        return shipLiveAfterBattle * this.getHp() - getCurrentHp();

    }

}
