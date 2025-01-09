package com.supergroup.kos.building.domain.model.battle;

import com.supergroup.kos.building.domain.constant.battle.FactionType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CheckWinBattleResult {
    private Boolean     isEnd = false;
    private FactionType factionTypeWin; // if null battle draw
}
