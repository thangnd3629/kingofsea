package com.supergroup.kos.mapper;

import org.mapstruct.Mapper;

import com.supergroup.kos.building.domain.model.mining.ScoutBuilding;
import com.supergroup.kos.dto.building.ScoutBuildingInfoResponse;

@Mapper
public interface ScoutBuildingMapper {
    ScoutBuildingInfoResponse toDTO(ScoutBuilding scoutBuilding);
}
