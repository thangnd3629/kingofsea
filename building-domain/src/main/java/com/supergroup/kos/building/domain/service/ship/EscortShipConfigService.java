package com.supergroup.kos.building.domain.service.ship;

import java.util.List;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.model.config.EscortShipConfig;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipConfigRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EscortShipConfigService {

    private final EscortShipConfigRepository escortShipConfigRepository;

    public List<EscortShipConfig> findByMilitaryLevelRequiredOrderByIdAsc(Long militaryLevelRequired) {
        return escortShipConfigRepository.findByMilitaryLevelRequiredOrderByIdAsc(militaryLevelRequired);
    }

    public List<EscortShipConfig> getAll() {
        return escortShipConfigRepository.findAll();
    }

}
