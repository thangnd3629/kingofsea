package com.supergroup.kos.dto.battle;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.supergroup.kos.building.domain.dto.seamap.CoordinatesDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MoveSessionResponse {
    private CoordinatesDTO start;
    private CoordinatesDTO destination;
    private Double         speed;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime  timeStart;
}
