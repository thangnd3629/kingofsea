package com.supergroup.kos.building.domain.dto.battle;

import java.io.Serializable;

import com.supergroup.kos.building.domain.model.battle.Battle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CreateTaskBattleEvent implements Serializable {
    private Battle battle;
}
