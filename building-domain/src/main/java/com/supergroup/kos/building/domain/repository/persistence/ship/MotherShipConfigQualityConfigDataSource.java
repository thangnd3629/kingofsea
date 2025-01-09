package com.supergroup.kos.building.domain.repository.persistence.ship;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.model.config.MotherShipConfigQualityConfig;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MotherShipConfigQualityConfigDataSource {

    private final MotherShipConfigQualityConfigRepository motherShipConfigQualityConfigRepository;

    @Cacheable(cacheNames = "MotherShipConfigQualityConfig", key = "'LIST'")
    public List<MotherShipConfigQualityConfig> getAll() {
        return motherShipConfigQualityConfigRepository.findByOrderByMotherShipConfigIdAndQualityAsc();
    }

    @Cacheable(cacheNames = "MotherShipConfigQualityConfig", key = "#id")
    public MotherShipConfigQualityConfig getById(Long id) {
        return motherShipConfigQualityConfigRepository.findById(id)
                                                      .orElseThrow(() -> KOSException.of(ErrorCode.MOTHER_SHIP_CONFIG_IS_NOT_FOUND));
    }

    @Cacheable(cacheNames = "MotherShipConfigQualityConfig", key = "#shipModelId.toString() + ':' + #qualityId.toString()")
    public MotherShipConfigQualityConfig getByModelIdAndQualityId(Long shipModelId, Long qualityId) {
        var listAll = getAll();
        return listAll.stream()
                      .filter(item -> item.getMotherShipQualityConfig().getId().equals(qualityId) && item.getMotherShipConfig().getId()
                                                                                                         .equals(shipModelId))
                      .findFirst()
                      .orElseThrow(() -> KOSException.of(ErrorCode.MOTHER_SHIP_CONFIG_IS_NOT_FOUND));
    }

}
