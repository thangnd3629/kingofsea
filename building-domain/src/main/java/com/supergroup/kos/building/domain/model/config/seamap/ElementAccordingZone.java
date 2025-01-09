package com.supergroup.kos.building.domain.model.config.seamap;

import java.util.List;

import com.supergroup.kos.building.domain.constant.seamap.ZoneSeaType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ElementAccordingZone {
    private ZoneSeaType zoneSeaType;
    private List<ElementAccording> elements;
}
