package com.supergroup.kos.dto.battle;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EscortShipReportResponse extends EscortShipResponse {
    private Long left;
    private Long lost;
    private Long add;
}
