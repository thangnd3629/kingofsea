package com.supergroup.kos.building.domain.model.config.battle;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleFieldLineUpConfig {
    private List<BattleFiledRowsConfig> rows;
}
