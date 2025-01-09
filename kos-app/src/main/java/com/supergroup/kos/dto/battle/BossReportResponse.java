package com.supergroup.kos.dto.battle;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BossReportResponse {
    private Long modelId;
    private Long currentHp;
    private Long hpLost;
    private Long maxHp;
}
