package com.supergroup.kos.building.domain.service.ship;

import java.util.List;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.model.config.EscortShipGroupConfig;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipGroupConfigDataSource;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EscortShipGroupConfigService {

    private final EscortShipGroupConfigDataSource escortShipGroupConfigDataSource;

    public List<EscortShipGroupConfig> getEscortShipGroupConfigs() {
        return escortShipGroupConfigDataSource.getAll();
    }
}
