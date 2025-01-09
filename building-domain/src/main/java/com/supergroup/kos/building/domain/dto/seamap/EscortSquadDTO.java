package com.supergroup.kos.building.domain.dto.seamap;

import javax.validation.constraints.Min;

import com.supergroup.kos.building.domain.constant.EscortShipType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EscortSquadDTO {
    private EscortShipType escortShipType;
    @Min(1)
    private Long           amount;
}
