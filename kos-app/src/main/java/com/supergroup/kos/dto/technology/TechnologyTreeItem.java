package com.supergroup.kos.dto.technology;

import java.util.List;

import com.supergroup.kos.building.domain.constant.TechnologyCode;
import com.supergroup.kos.dto.RequirementDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TechnologyTreeItem {
    private Boolean              isLock;
    private Boolean              isResearched;
    private Boolean              isResearchable;
    private String               thumbnail;
    private TechnologyCode       code;
    private String               effect;
    private String               name;
    private String               id;
    private RequirementDTO       requirement;
    private List<TechnologyCode> conditions;
}