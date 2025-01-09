package com.supergroup.kos.building.domain.model.battle;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattlePvEStatic {
    private Long totalAtk1Dealt = 0L;
    private Long totalAtk2Dealt = 0L;
}
