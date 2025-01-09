package com.supergroup.kos.dto.scout;

import java.time.LocalDateTime;

import com.supergroup.kos.building.domain.constant.MissionResult;
import com.supergroup.kos.building.domain.constant.MissionType;
import com.supergroup.kos.building.domain.dto.seamap.CoordinatesDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ScoutReportResponse {
    private Long           id;
    private CoordinatesDTO location;
    private CoordinatesDTO navigate;
    private Long           missionTime;
    private LocalDateTime  timeDone;
    private MissionResult  result;
    private MissionType    missionType;
    private Boolean        isBookmark;
    private Boolean        isSeen;
    private String         name;
}
