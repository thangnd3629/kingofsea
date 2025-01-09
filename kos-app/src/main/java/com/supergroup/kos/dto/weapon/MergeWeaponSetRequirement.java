package com.supergroup.kos.dto.weapon;

import com.supergroup.kos.dto.technology.TechnologyDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MergeWeaponSetRequirement {
    private Long          gold;
    private TechnologyDTO technology;
}
