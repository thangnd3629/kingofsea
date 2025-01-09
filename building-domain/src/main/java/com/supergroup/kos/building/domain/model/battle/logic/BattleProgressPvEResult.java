package com.supergroup.kos.building.domain.model.battle.logic;

import java.util.ArrayList;
import java.util.List;

import com.supergroup.kos.building.domain.constant.battle.FactionType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleProgressPvEResult {
    private List<BattleProgress> battleProgresses = new ArrayList<>();
    private FactionType          factionTypeWin;
}
