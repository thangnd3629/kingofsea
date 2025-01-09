package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.model.seamap.movesession.MissionType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class InitSeaActivityCommand {
    private Long        destinationId;
    private Long        kosProfileId;
    private Long        lineUpId;
    private MissionType missionType;
}
