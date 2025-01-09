package com.supergroup.admin.domain.command;

import com.supergroup.kos.building.domain.constant.TechnologyCode;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AdminUpdateUserTechnologyCommand {
    private Boolean        isResearched;
    private Boolean        isLock;
    private Long           kosProfileId;
    private TechnologyCode technologyCode;
}
