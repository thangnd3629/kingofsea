package com.supergroup.kos.building.domain.dto.seamap;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.supergroup.kos.building.domain.constant.seamap.MoveSessionType;
import com.supergroup.kos.building.domain.constant.seamap.ResourceIslandType;
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
public class MoveSessionDTO {
    private String             uuid;
    private Long               id;
    private Coordinates        start;
    private Coordinates        end;
    private Double             speed;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime      timeStart;
    private Long               seaActivityId;
    private Long               destinationElementId;
    private SeaElementType     destinationType;
    private ResourceIslandType resourceType;
    private MissionType        missionType;

    /** legacy code, to be removed in SP7.5 **/
    private MoveSessionType type;
}
