package com.supergroup.kos.dto.weapon;

import java.util.List;

import com.supergroup.kos.building.domain.constant.WeaponStat;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class WeaponSetConfigResponse {
    private Long                       id;
    private String                     name;
    private String                     description;
    private String                     thumbnail;
    private Long                       qualityExist;
    private MergeWeaponSetRequirement  requirement;
    private List<WeaponConfigResponse> weaponModels;
    private Long                       stat;
    private WeaponStat                 statType;
}
