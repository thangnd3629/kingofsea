package com.supergroup.kos.building.domain.repository.persistence.ship;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.model.config.EscortShipLevelConfig;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EscortShipLevelConfigDataSource {

    private final EscortShipLevelConfigRepository escortShipLevelConfigRepository;

    @Cacheable(cacheNames = "EscortShipLevelConfig", key = "#type.name() + ':LIST'")
    public List<EscortShipLevelConfig> getByType(EscortShipType type) {
        return escortShipLevelConfigRepository.findByTypeOrderByLevelAsc(type);
    }

    @Cacheable(cacheNames = "EscortShipLevelConfig", key = "#type.name() + ':' + #level.toString()")
    public EscortShipLevelConfig getByTypeAndLevel(EscortShipType type, Long level) {
        return escortShipLevelConfigRepository.findByTypeAndLevel(type, level)
                                              .orElseThrow(() -> KOSException.of(ErrorCode.ESCORT_SHIP_LEVEL_CONFIG_IS_NOT_FOUND));
    }

}
