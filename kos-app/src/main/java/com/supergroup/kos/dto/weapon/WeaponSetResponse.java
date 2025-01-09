package com.supergroup.kos.dto.weapon;

import com.supergroup.kos.building.domain.constant.WeaponSetLevel;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class WeaponSetResponse {
    private Long                    id;
    private WeaponSetLevel          quality;
    private Double                  percentStat;
    private String                  description;
    private WeaponSetConfigResponse model;
    private Long                    motherShipId;

}
