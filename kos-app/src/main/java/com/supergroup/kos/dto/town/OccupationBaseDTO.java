package com.supergroup.kos.dto.town;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class OccupationBaseDTO {
    private Long          elementId;
    private Long          elementConfigId;
    private String        name;
    private Long          level;
    private String        avatarUrl;
    private Long          x;
    private Long          y;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timeStart;
    private List<Long>    activityIds;
}
