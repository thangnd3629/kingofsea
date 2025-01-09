package com.supergroup.kos.building.domain.service.ship;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.constant.EscortShipGroupLevel;
import com.supergroup.kos.building.domain.constant.EscortShipGroupName;
import com.supergroup.kos.building.domain.model.config.EscortShipGroupLevelConfig;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipGroupConfigLevelDataSource;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipGroupLevelConfigRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EscortShipGroupLevelConfigService {
    private final EscortShipGroupConfigLevelDataSource escortShipGroupConfigLevelDataSource;
    private final EscortShipGroupLevelConfigRepository escortShipGroupLevelConfigRepository;

    public List<EscortShipGroupLevelConfig> getEscortShipGroupLevelConfigsFilter(EscortShipGroupLevel level, EscortShipGroupName shipGroupName) {
        var configs = escortShipGroupConfigLevelDataSource.getAll();
        return configs.stream().filter(config -> (level == null || config.getLevel().equals(level))
                                                 &&
                                                 (shipGroupName == null || config.getShipGroupName().equals(shipGroupName)))
                      .collect(Collectors.toList());
    }
    public List<EscortShipGroupLevelConfig> getByArmoryBuildingConfigId(Long id) {
        return escortShipGroupLevelConfigRepository.findByArmoryBuildingConfigId(id);
    }
}
