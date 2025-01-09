package com.supergroup.admin.domain.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.supergroup.admin.domain.command.AdminUpdateBuildingCommand;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminBuildingService {

    private final BuildingRepository       buildingRepository;
    private final BuildingConfigRepository buildingConfigRepository;

    public void updateBuilding(AdminUpdateBuildingCommand command) {
        if (Objects.nonNull(command.getLevel())) {
            var existsConfig = buildingConfigRepository.existsByBuildingNameAndLevel(command.getBuildingName(), command.getLevel());
            if (existsConfig.equals(true)) {
                var building = buildingRepository.get(command.getBuildingName(), command.getKosProfileId());
                building.setLevel(command.getLevel()).setUpgradeSession(null);
                buildingRepository.save(building);
            } else {
                throw KOSException.of(ErrorCode.CONFIG_NOT_FOUND);
            }
        }
    }
}
