package com.supergroup.kos.dto.building;

import java.util.List;

import com.supergroup.kos.dto.technology.TechnologyTypeResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ResearchBuildingInfoResponse {
    private Long                         level;
    private Long                         gloryPoint;
    private List<TechnologyTypeResponse> technology;
    private Long                         techPoint;
}