package com.supergroup.admin.dto;

import com.supergroup.kos.building.domain.constant.battle.BattleStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SwitchStatusRequest {
    private Long         kosProfileId;
    private BattleStatus battleStatus;
}

