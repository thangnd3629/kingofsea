package com.supergroup.kos.building.domain.model.battle.logic;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true)
public class AttackDamage {
    private Long atk1;
    private Long atk2;

    public AttackDamage setAtk1(Long atk1) {
        this.atk1 = atk1 >= 0 ? atk1 :0;
        return this;
    }

    public AttackDamage setAtk2(Long atk2) {
        this.atk2 = atk2 >= 0 ? atk2 : 0;
        return this;
    }
}
