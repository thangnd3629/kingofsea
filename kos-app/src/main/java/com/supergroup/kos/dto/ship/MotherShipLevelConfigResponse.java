package com.supergroup.kos.dto.ship;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MotherShipLevelConfigResponse {
    private Long   level;
    private Double percentStat;
    private Long   wood;
    private Long   stone;
    private Long   gold;
    private Long   upgradeDuration;
}
