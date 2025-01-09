

package com.supergroup.kos.dto.ship;

import com.supergroup.kos.building.domain.constant.EscortShipType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EscortShipConfigResponse {
    private EscortShipType type;
    private String         name;
    private String         thumbnail;
    private Long           atk1;
    private Long           atk2;
    private Long           def1;
    private Long           def2;
    private Long           hp;
    private Long           dodge;
}

