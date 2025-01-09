package com.supergroup.kos.building.domain.task;

import com.supergroup.kos.building.domain.constant.battle.BattleType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class BattleTask {
    private Long       battleId;
    private String     defenderName;
    private BattleType battleType;
    private Long       battleFieldId;
}
