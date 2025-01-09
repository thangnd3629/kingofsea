package com.supergroup.kos.building.domain.model.config.battle;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BattleRewardItem {
    private Long    start      = 0L;
    private Long    end        = 0L;
    private Double  percent    = 0.0;
    private Integer numRelic   = 0;
    private Integer numQueen   = 0;
    private Integer numWeapon  = 0;
    private Long    levelRelic = 0L;
}
