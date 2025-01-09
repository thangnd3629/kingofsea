package com.supergroup.kos.building.domain.model.battle.logic;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleProgress implements Serializable {
    private Long   round;
    private Attack attacker;
    private Attack defender;
}
