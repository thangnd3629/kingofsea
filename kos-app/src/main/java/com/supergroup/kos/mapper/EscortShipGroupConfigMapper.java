package com.supergroup.kos.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.supergroup.kos.building.domain.model.config.EscortShipGroupConfig;
import com.supergroup.kos.dto.ship.EscortShipGroupConfigResponse;

@Mapper
public interface EscortShipGroupConfigMapper {

    EscortShipGroupConfigResponse toDTO(EscortShipGroupConfig escortShipGroupConfig);

    List<EscortShipGroupConfigResponse> toDTOs(List<EscortShipGroupConfig> escortShipGroupConfigs);
}
