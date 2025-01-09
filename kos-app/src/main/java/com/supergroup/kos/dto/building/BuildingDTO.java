package com.supergroup.kos.dto.building;

import com.supergroup.kos.building.domain.constant.BuildingName;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BuildingDTO {
    private Long         level;
    private BuildingName name;
}