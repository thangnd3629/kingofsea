package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.constant.BuildingName;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class GetUpgradeStatusCommand {
    private Long         kosProfileId;
    private BuildingName buildingName;
}
