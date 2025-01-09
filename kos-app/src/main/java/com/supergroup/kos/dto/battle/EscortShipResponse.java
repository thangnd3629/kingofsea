package com.supergroup.kos.dto.battle;

import java.util.Objects;

import com.supergroup.kos.building.domain.constant.EscortShipGroupName;
import com.supergroup.kos.building.domain.constant.EscortShipType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EscortShipResponse {
    private Long                modelId;
    private EscortShipType      shipType;
    private EscortShipGroupName groupName;
    private Long                left;
    private Integer             index;

    public EscortShipResponse setShipType(EscortShipType type) {
        this.shipType = type;
        this.index = Objects.nonNull(type) ? type.getIndexInCombat() : null;
        return this;
    }
}
