package com.supergroup.kos.dto.seamap.activity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.supergroup.kos.building.domain.constant.seamap.SeaActivityStatus;
import com.supergroup.kos.building.domain.dto.seamap.MoveSessionDTO;
import com.supergroup.kos.dto.seamap.Coordinates;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeaActivityDTO {
    private Long              id;
    private Coordinates       currentLocation;
    private SeaActivityStatus status;
    private Long              currentRound;
    private MoveSessionDTO    activeMoveSession;
    private ShipLineUpDTO     shipLineUp;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime     timeEnd;

}
