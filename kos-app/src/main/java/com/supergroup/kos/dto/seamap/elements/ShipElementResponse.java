package com.supergroup.kos.dto.seamap.elements;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.supergroup.kos.building.domain.constant.seamap.MoveSessionType;
import com.supergroup.kos.building.domain.dto.seamap.CoordinatesDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipElementResponse extends ElementResponse {
    private Double          speed;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime   timeStart;
    private CoordinatesDTO  start;
    private CoordinatesDTO  end;
    private Long            kosProfileId;
    private MoveSessionType shipStatus;
}
