package com.supergroup.kos.building.domain.model.config.battle;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DefBattleConfig {
    private Double defPercent;
    private Double dodgePercent;
}
