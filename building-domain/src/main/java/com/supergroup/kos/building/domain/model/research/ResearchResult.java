package com.supergroup.kos.building.domain.model.research;

import com.supergroup.kos.building.domain.constant.research.TargetType;
import com.supergroup.kos.building.domain.constant.research.UnitType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
//@RequiredArgsConstructor
@Accessors(chain = true)
public class ResearchResult {
    private  TargetType targetType;
    private  UnitType   unitType;
    private  Object     value;
}
