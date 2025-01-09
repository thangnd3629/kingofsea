package com.supergroup.admin.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.supergroup.admin.dto.EscortShipSquadDTO;
import com.supergroup.kos.building.domain.model.seamap.EscortShipSquad;

@Mapper
public interface EscortShipModelMapper {
    @Mapping(source = "escortShip.escortShipConfig", target = "escortShip.model")
    EscortShipSquadDTO toDTO(EscortShipSquad model);
}
