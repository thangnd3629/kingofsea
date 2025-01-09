package com.supergroup.kos.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.model.ship.EscortShip;
import com.supergroup.kos.dto.ship.EscortShipStatisticResponse;

@Mapper

public interface EscortShipStatisticMapper {
    @Mapping(target = "type", source = "escortShip.escortShipConfig.type")
    @Mapping(target = "name", source = "escortShip.escortShipConfig.type", qualifiedByName = "getName")
    @Mapping(target = "amount", source = "escortShip.amount")
    @Mapping(target = "totalAttack", source = "escortShip", qualifiedByName = "getTotalAttack")
    EscortShipStatisticResponse toDTO(EscortShip escortShip);

    List<EscortShipStatisticResponse> toDTOs(List<EscortShip> escortShips);

    @Named("getName")
    default String getName(EscortShipType type) {
        return type.getEscortShipTypeName();
    }

    @Named("getTotalAttack")
    default Long getName(EscortShip escortShip) {
        return Math.round((escortShip.getEscortShipConfig().getAtk1() + escortShip.getEscortShipConfig().getAtk2())
                          * escortShip.getAmount()
                          * escortShip.getPercentLevelStat()
                          * escortShip.getPercentQualityStat());
    }
}
