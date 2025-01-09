
package com.supergroup.kos.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.supergroup.kos.building.domain.model.ship.EscortShipGroup;
import com.supergroup.kos.dto.ship.EscortShipGroupResponse;

@Mapper
public interface EscortShipGroupMapper {

    @Mapping(target = "groupName", source = "escortShipGroup.escortShipGroupLevelConfig.escortShipGroupConfig.name")
    @Mapping(target = "quality", source = "escortShipGroup.escortShipGroupLevelConfig.level")
    @Mapping(target = "percentStat", source = "escortShipGroup.escortShipGroupLevelConfig.percentStat")
    EscortShipGroupResponse toDTO(EscortShipGroup escortShipGroup);
    List<EscortShipGroupResponse> toDTOs(List<EscortShipGroup> EscortShipGroups);
}
