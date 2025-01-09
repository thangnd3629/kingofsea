package com.supergroup.kos.building.domain.repository.persistence.ship;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.model.config.EscortShipConfig;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EscortShipConfigDataSource {

    private final EscortShipConfigRepository escortShipConfigRepository;

    @Cacheable(cacheNames = "EscortShipConfig", key = "'LIST'")
    public List<EscortShipConfig> getAll() {
        return escortShipConfigRepository.findByOrderByIdAsc();
    }

    @Cacheable(cacheNames = "EscortShipConfig", key = "#type.name()")
    public EscortShipConfig getByType(EscortShipType type) {
        return escortShipConfigRepository.findByType(type)
                                         .orElseThrow(() -> KOSException.of(ErrorCode.ESCORT_SHIP_CONFIG_IS_NOT_FOUND));
    }
}
