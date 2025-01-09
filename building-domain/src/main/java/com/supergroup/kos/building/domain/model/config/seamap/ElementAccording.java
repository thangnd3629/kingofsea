package com.supergroup.kos.building.domain.model.config.seamap;

import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ElementAccording {
    private SeaElementType type;
    private Long           elementConfigId;
    private Long           quantity;
}
