package com.supergroup.admin.domain.service;

import org.springframework.stereotype.Service;

import com.supergroup.admin.domain.command.AdminCreateEscortShipCommand;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.model.ship.EscortShip;
import com.supergroup.kos.building.domain.repository.persistence.building.MilitaryBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipConfigRepository;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminEscortShipService {

    private final EscortShipRepository       escortShipRepository;
    private final EscortShipConfigRepository escortShipConfigRepository;
    private final MilitaryBuildingRepository militaryBuildingRepository;

    public EscortShip createEscortShip(AdminCreateEscortShipCommand command) {
        var model = escortShipConfigRepository.findById(command.getModelId())
                                              .orElseThrow(() -> KOSException.of(ErrorCode.ESCORT_SHIP_CONFIG_IS_NOT_FOUND));
        var militaryBuilding = militaryBuildingRepository.findByKosProfileId(command.getKosProfileId())
                                                         .orElseThrow(() -> KOSException.of(ErrorCode.MILITARY_BUILDING_IS_NOT_FOUND));
        var escortShip = new EscortShip()
                .setEscortShipConfig(model);
//                .setMilitaryBuilding(militaryBuilding);
        return escortShipRepository.save(escortShip);
    }
}
