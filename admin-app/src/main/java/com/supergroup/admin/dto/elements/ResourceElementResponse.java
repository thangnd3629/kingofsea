package com.supergroup.admin.dto.elements;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ResourceElementResponse extends ElementResponse {
    private Double        mined;
    private String        status;
    private Long          kosProfileId;
    private Double        collectedResource;
    private Double        speedBoost = 1.;
    private Double        tonnage;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timeStart;
}
