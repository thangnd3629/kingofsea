package com.supergroup.kos.dto.battle;

import com.supergroup.kos.building.domain.constant.MotherShipQualityKey;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MotherShipResponse {
    private Long                 modelId;
    private String               owner;
    private MotherShipQualityKey quality;
    private Boolean              isWithdrawal = false;
}
