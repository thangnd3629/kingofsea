package com.supergroup.admin.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.supergroup.admin.dto.ShipLineUpDTO;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;

@Mapper(uses = { EscortShipModelMapper.class})
public interface LineUpMapper {
    @Mapping(source = "motherShip.motherShipConfigQualityConfig.motherShipConfig.name", target = "motherShip.name")
    ShipLineUpDTO toDto(ShipLineUp lineUp);
}
