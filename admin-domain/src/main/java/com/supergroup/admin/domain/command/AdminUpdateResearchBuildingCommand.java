package com.supergroup.admin.domain.command;

import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateResearchBuildingCommand {
    @Min(0)
    private Long level;
}
