package com.supergroup.kos.building.domain.model.config.battle;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleRewardConfig implements Serializable {
    List<BattleRewardItem> items;
}
