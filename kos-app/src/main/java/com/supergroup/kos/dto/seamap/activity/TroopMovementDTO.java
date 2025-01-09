package com.supergroup.kos.dto.seamap.activity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.movesession.MissionType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
@Accessors(chain = true)
public class TroopMovementDTO {
    private Long           id;
    private Coordinates    start;
    private Coordinates    end;
    private Double         speed;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime  timeStart;
    private Long           seaActivityId;
    private SeaElementType seaElementType;
    private MissionType    missionType;
    private ShipLineUpDTO  lineUp;
}
