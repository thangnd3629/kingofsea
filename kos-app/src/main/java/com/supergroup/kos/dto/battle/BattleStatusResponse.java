package com.supergroup.kos.dto.battle;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleStatusResponse {
    private Boolean                     beingAttacked;
    private List<BattleProfileResponse> attackers;
}
