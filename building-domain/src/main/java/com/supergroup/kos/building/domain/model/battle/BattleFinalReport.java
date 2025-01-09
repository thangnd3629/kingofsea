package com.supergroup.kos.building.domain.model.battle;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Embeddable
@Accessors(chain = true)
public class BattleFinalReport implements Serializable {
    private Long totalAtk1        = 0L;
    private Long totalAtk2        = 0L;
    private Long takenAtk1        = 0L;
    private Long takenAtk2        = 0L;
    private Long escortShipLost   = 0L;
    private Long motherShipHpLost = 0L;
    private Long motherShipDied   = 0L;
    private Long amountAlly       = 0L;
    private Long amountItem       = 0L;
    private Long npcLostHp        = 0L;
}
