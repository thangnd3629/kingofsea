package com.supergroup.kos.building.domain.model.seamap;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RefreshNpcAndMineResult {
    private Integer       totalElementDeleted;
    private Integer       totalElementNotDeleted;
    private Integer       totalElementAccordingBaseCreated;
    private Integer       totalElementAccordingZoneSeaCreated;
}
