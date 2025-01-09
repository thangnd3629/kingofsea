package com.supergroup.kos.dto.battle;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleReportDetailResponse {
    private FinalReportResponse attacker;
    private FinalReportResponse defender;
    private ResourceResponse    victoryReward;
    private ResourceResponse    defeatLoss;
    private List<Long>          roundIds;
}
