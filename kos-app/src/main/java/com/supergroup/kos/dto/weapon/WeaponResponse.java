package com.supergroup.kos.dto.weapon;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class WeaponResponse {
    private Long                 id;
    private WeaponConfigResponse model;
    private Long                 motherShipId;

}
