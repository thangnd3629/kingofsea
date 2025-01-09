package com.supergroup.kos.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.supergroup.kos.building.domain.model.ship.EscortShip;
import com.supergroup.kos.dto.ship.EscortShipResponse;

;

@Mapper
public interface EscortShipMapper {

    @Mapping(target = "amount", source = "escortShip.amount")
    @Mapping(target = "level", source = "escortShip.level")

    @Mapping(target = "model.type", source = "escortShip.escortShipConfig.type")
    @Mapping(target = "model.name", source = "escortShip.escortShipConfig.name")
    @Mapping(target = "model.thumbnail", source = "escortShip.escortShipConfig.thumbnail")
    @Mapping(target = "model.atk1", source = "escortShip.escortShipConfig.atk1")
    @Mapping(target = "model.atk2", source = "escortShip.escortShipConfig.atk2")
    @Mapping(target = "model.def1", source = "escortShip.escortShipConfig.def1")
    @Mapping(target = "model.def2", source = "escortShip.escortShipConfig.def2")
    @Mapping(target = "model.hp", source = "escortShip.escortShipConfig.hp")
    @Mapping(target = "model.dodge", source = "escortShip.escortShipConfig.dodge")

    @Mapping(target = "shipGroup.groupName", source = "escortShip.escortShipConfig.escortShipGroupConfig.name")
    @Mapping(target = "shipGroup.quality", source = "escortShip.escortShipGroup.escortShipGroupLevelConfig.level")
    @Mapping(target = "shipGroup.percentStat", source = "escortShip.escortShipGroup.escortShipGroupLevelConfig.percentStat")
    EscortShipResponse toDTO(EscortShip escortShip);

    List<EscortShipResponse> toDTOs(List<EscortShip> escortShips);
}
