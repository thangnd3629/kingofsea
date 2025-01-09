package com.supergroup.kos.mapper;

import org.mapstruct.Mapper;

import com.supergroup.kos.building.domain.model.building.VaultBuilding;
import com.supergroup.kos.dto.building.VaultBuildingResponse;

@Mapper
public interface VaultBuildingMapper {
    VaultBuildingResponse toDTO(VaultBuilding vaultBuilding);
}
