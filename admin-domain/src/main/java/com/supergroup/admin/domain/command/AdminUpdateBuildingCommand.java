package com.supergroup.admin.domain.command;

import javax.validation.constraints.Min;

import com.supergroup.kos.building.domain.constant.BuildingName;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AdminUpdateBuildingCommand {
    @Min(0)
    private Long level;
    private Long kosProfileId;
    private BuildingName buildingName;
}
