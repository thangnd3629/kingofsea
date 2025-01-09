package com.supergroup.kos.building.domain.model.battle;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BossBattle extends BattleUnit {
    private Long bossId;
}
