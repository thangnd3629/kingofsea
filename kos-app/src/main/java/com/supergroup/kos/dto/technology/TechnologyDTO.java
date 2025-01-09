package com.supergroup.kos.dto.technology;

import com.supergroup.kos.building.domain.constant.TechnologyCode;
import com.supergroup.kos.building.domain.constant.TechnologyType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TechnologyDTO {
    private String         name;
    private TechnologyCode code;
    private TechnologyType type;
    private Boolean        isResearched;
}
