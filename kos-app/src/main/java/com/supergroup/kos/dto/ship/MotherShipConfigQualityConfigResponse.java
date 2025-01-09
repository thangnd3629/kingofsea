package com.supergroup.kos.dto.ship;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MotherShipConfigQualityConfigResponse {
    private Long                     id;
    private BuyShipRequirement       buyRequirement;
    private MotherShipConfigResponse model;
    private MotherShipQualityConfigResponse qualityInfo;
}
