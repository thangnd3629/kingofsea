package com.supergroup.kos.building.domain.repository.persistence.ship;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.model.config.EscortShipGroupConfig;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EscortShipGroupConfigDataSource {

    private final EscortShipGroupConfigRepository escortShipGroupConfigRepository;

    @Cacheable(cacheNames = "EscortShipGroupConfig", key = "'LIST'")
    public List<EscortShipGroupConfig> getAll() {
        return escortShipGroupConfigRepository.findAll();
    }

    @Cacheable(cacheNames = "EscortShipGroupConfig", key = "#id")
    public EscortShipGroupConfig getById(Long id) {
        return escortShipGroupConfigRepository.findById(id)
                                              .orElseThrow(() -> KOSException.of(ErrorCode.ESCORT_SHIP_GROUP_CONFIG_IS_NOT_FOUND));
    }

}
