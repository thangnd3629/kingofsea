package com.supergroup.kos.dto.upgrade;

import com.supergroup.kos.dto.technology.TechnologyDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpgradeEscortShipTypeRequirement extends UpgradeRequirement {
    private TechnologyDTO technology;
}
