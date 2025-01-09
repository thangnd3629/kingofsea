package com.supergroup.kos.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.supergroup.kos.building.domain.model.config.MotherShipConfig;
import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.dto.ship.MotherShipConfigResponse;
import com.supergroup.kos.dto.ship.MotherShipResponse;

@Mapper
public interface MotherShipConfigMapper {
    MotherShipConfigResponse toDTO(MotherShipConfig motherShip);

    List<MotherShipResponse> toDTOs(List<MotherShip> motherShips);
}
