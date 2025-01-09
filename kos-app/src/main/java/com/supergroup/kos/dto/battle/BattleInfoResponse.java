package com.supergroup.kos.dto.battle;

import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.constant.battle.BattleType;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleInfoResponse {
    private Long           id;
    private Long         currentRound;
    private BattleType   battleType;
    private BattleStatus status;
    private Coordinates    battleSite;
    private AttackResponse attacker;
    private AttackResponse defender;

}
