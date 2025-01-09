package com.supergroup.kos.dto.scout;

import java.time.LocalDateTime;

import com.supergroup.kos.building.domain.model.scout.Location;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ScoutReportResponseDetail {
    private Long                   id;
//    private String                 username;
//    private String                 avatarUrl;
//    private Location               location;
    private Long                   missionTime;
    private LocalDateTime          timeDone;
    private ScoutingResultResponse scoutingResult;
}
