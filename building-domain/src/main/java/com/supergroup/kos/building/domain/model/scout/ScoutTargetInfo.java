package com.supergroup.kos.building.domain.model.scout;

import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ScoutTargetInfo {
    private KosProfile kosProfileTarget;
    private SeaElement seaElement;
}
