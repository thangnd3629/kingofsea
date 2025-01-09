package com.supergroup.kos.dto.weapon;

import java.util.List;

import com.supergroup.kos.building.domain.constant.WeaponStat;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class WeaponConfigResponse {
    private Long       id;
    private String     name;
    private String     description;
    private String     thumbnail;
    private Long       qualityExist;
    private List<Long> equippedOnShips;
    private Long       stat;
    private WeaponStat statType;

}
