package com.supergroup.kos.building.domain.model.config.seamap;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SeaMapConfig {
    private List<ElementAccording>     elementAccordingBaseUser;
    private List<ElementAccordingZone> elementAccordingZones;
}
