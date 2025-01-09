package com.supergroup.kos.dto.building;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ScoutBuildingInfoResponse {
    private Long   level;
    private String name;
    private Long   totalScout;
    private Long   capacity;
    private Long   numberMission;
    private Long   availableScout;
}
