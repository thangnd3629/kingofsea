package com.supergroup.kos.building.domain.dto.battle;

import java.io.Serializable;

import com.supergroup.kos.building.domain.constant.battle.FactionType;
import com.supergroup.kos.building.domain.model.battle.Battle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
public class EndBattleEvent implements Serializable {
    private Long      battleId;
    private FactionType winner;
}
