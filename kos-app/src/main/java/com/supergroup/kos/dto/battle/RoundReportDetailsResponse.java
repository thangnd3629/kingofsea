package com.supergroup.kos.dto.battle;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RoundReportDetailsResponse {
    private DamageRoundReportResponse attacker;
    private DamageRoundReportResponse defender;
}
