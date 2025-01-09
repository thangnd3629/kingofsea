package com.supergroup.kos.dto.technology;

import com.supergroup.kos.building.domain.constant.research.TargetType;
import com.supergroup.kos.building.domain.constant.research.UnitType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResearchTechnologyResponse {
    private TargetType targetType;
    private UnitType   unitType;
    private Object     value;
}
