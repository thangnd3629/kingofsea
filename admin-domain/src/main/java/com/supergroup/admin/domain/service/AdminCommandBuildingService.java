package com.supergroup.admin.domain.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.supergroup.admin.domain.command.AdminUpdateCommandBuildingCommand;
import com.supergroup.admin.domain.repository.AdminCommandBuildingRepository;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminCommandBuildingService {

    private final AdminCommandBuildingRepository adminCommandBuildingRepository;

    public void updateBuilding(AdminUpdateCommandBuildingCommand command) {
        var maxSlotWeaponOfMotherShip = command.getMaxSlotWeaponOfMotherShip();
        var commandPort = adminCommandBuildingRepository.findByKosProfileId(command.getKosProfileId())
                                                        .orElseThrow(() -> KOSException.of(ErrorCode.COMMAND_BUILDING_IS_NOT_FOUND));
        if (Objects.nonNull(maxSlotWeaponOfMotherShip)) {
            commandPort.setMaxSlotWeaponOfMotherShip(maxSlotWeaponOfMotherShip);
        }
        if (Objects.nonNull(command.getLevel())) {
            commandPort.setLevel(command.getLevel());
        }
        adminCommandBuildingRepository.save(commandPort);
    }
}
