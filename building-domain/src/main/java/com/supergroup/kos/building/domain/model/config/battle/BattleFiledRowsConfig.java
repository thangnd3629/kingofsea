package com.supergroup.kos.building.domain.model.config.battle;

import java.util.List;

import com.supergroup.kos.building.domain.constant.EscortShipGroupName;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleFiledRowsConfig {
    private EscortShipGroupName shipGroup;
    private List<BattleFiledElementConfig> elements;
}
