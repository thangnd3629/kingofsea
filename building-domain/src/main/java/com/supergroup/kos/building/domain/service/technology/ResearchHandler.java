package com.supergroup.kos.building.domain.service.technology;

import java.util.List;

import com.supergroup.kos.building.domain.model.research.ResearchResult;
import com.supergroup.kos.building.domain.model.technology.Technology;
import com.supergroup.kos.building.domain.model.technology.UserTechnology;

public interface ResearchHandler {

    List<ResearchResult> research(UserTechnology userTechnology);

    default ResearchResult createDefaultResult(Technology technology) {
        return new ResearchResult()
                .setTargetType(technology.getTargetType())
                .setUnitType(technology.getUnitType());
    }

}
