package com.supergroup.kos.dto.seamap.activity;

import javax.validation.constraints.NotNull;

import com.supergroup.kos.building.domain.model.seamap.movesession.MissionType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeployTroopRequest {
    @NotNull
    private Long destinationId;
    @NotNull
    private Long lineUpId;
    private MissionType missionType;
}
