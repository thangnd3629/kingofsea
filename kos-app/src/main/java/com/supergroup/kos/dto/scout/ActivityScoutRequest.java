package com.supergroup.kos.dto.scout;

import javax.validation.constraints.Min;

import com.supergroup.kos.building.domain.constant.MissionType;
import com.supergroup.kos.dto.seamap.Coordinates;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ActivityScoutRequest {
    @Min(1)
    private Long        numberArmy;
    private MissionType type;
    private Coordinates location;
}
