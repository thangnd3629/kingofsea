package com.supergroup.kos.building.domain.service.battle;

import org.springframework.transaction.annotation.Transactional;

import com.supergroup.kos.building.domain.constant.battle.FactionType;
import com.supergroup.kos.building.domain.model.battle.Battle;

public interface BattleHandler {
    @Transactional
    void onBattleEnded(Battle battle, FactionType factionTypeWin);
}
