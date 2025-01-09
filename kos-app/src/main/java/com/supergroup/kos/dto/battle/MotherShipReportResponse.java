package com.supergroup.kos.dto.battle;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MotherShipReportResponse extends MotherShipResponse {
    private Long currentHp;
    private Long hpLost;
    private Long maxHp;
    private Long index;
}
