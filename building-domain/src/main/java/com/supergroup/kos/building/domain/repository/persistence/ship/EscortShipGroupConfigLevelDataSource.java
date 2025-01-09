package com.supergroup.kos.building.domain.repository.persistence.ship;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.supergroup.kos.building.domain.constant.EscortShipGroupLevel;
import com.supergroup.kos.building.domain.model.config.EscortShipGroupLevelConfig;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EscortShipGroupConfigLevelDataSource {
    private final EscortShipGroupLevelConfigRepository escortShipGroupLevelConfigRepository;

    @Cacheable(cacheNames = "EscortShipGroupLevelConfig", key = "'LIST'")
    public List<EscortShipGroupLevelConfig> getAll() {
        return escortShipGroupLevelConfigRepository.findByOrderByShipGroupConfigNameAscLevelAsc();
    }

    @Cacheable(cacheNames = "EscortShipGroupLevelConfig", key = "#level.name()")
    public List<EscortShipGroupLevelConfig> getByGroupConfigLevel(EscortShipGroupLevel level) {
        return getAll().stream()
                       .filter(config -> config.getLevel().equals(level))
                       .collect(Collectors.toList());
    }
}
