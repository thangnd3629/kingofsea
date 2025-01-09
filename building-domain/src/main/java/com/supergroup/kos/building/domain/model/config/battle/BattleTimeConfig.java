package com.supergroup.kos.building.domain.model.config.battle;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleTimeConfig {
    private BattleTimeConfigDetail attack; // attack and occupy
    private BattleTimeConfigDetail monster;
    private BattleTimeConfigDetail mine;
    private BattleTimeConfigDetail liberate;
}
