package com.supergroup.kos.building.domain.model.battle;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BossSeaBattle extends BattleUnit {
    private Long elementId;
    private Long bossConfigId;
    private Long hpAfterBattle;
}
