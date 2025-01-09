package com.supergroup.kos.dto.battle;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class FinalReportResponse {
    private Long             totalAtk1;
    private Long             totalAtk2;
    private Long             takenAtk1;
    private Long             takenAtk2;
    private Long             escortShipLost;
    private Long             motherShipHpLost;
    private Long             motherShipDied;
    private Long             amountAlly;
    private Long             amountItem;
    private Long             npcLostHp;
}
