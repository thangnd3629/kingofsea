

package com.supergroup.kos.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.supergroup.kos.building.domain.model.building.ArmoryBuilding;
import com.supergroup.kos.dto.building.ArmoryBuildingResponse;

@Mapper(uses = EscortShipGroupMapper.class)
public interface ArmoryBuildingMapper {
    ArmoryBuildingResponse toDTO(ArmoryBuilding armoryBuilding);
    List<ArmoryBuildingResponse> toDTOs(List<ArmoryBuilding> armoryBuilding);
}
