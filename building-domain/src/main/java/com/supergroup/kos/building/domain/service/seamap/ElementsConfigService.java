package com.supergroup.kos.building.domain.service.seamap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.model.config.seamap.SeaElementConfig;
import com.supergroup.kos.building.domain.model.config.seamap.ShipElementConfig;
import com.supergroup.kos.building.domain.model.seamap.OccupiedArea;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementConfigRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ElementsConfigService {
    private final SeaElementConfigRepository<SeaElementConfig> seaElementConfigRepository;

    @Cacheable(cacheNames = "SeaElementConfig", key = "'LIST'")
    public List<SeaElementConfig> getAllElementsConfig() {
        return seaElementConfigRepository.getAllElementsConfig();
    }

    @CacheEvict(cacheNames = "SeaElementConfig", key = "'LIST'")
    public void deleteConfigCache() {}

    @Cacheable(cacheNames = "SeaElementConfig", key = "'AreaConfig'")
    public Map<Long, OccupiedArea> getElementOccupiedAreaConfig() {
        List<SeaElementConfig> elementsConfigs = getAllElementsConfig();
        Map<Long, OccupiedArea> mapElementConfig = new HashMap<>();
        for (SeaElementConfig e : elementsConfigs) {
            mapElementConfig.put(e.getId(), e.getOccupied());
        }
        return mapElementConfig;
    }

    @Cacheable(cacheNames = "SeaElementConfig", key = "'MapElementConfig'")
    public Map<Long, SeaElementConfig> getMapElementConfig() {
        List<SeaElementConfig> elementsConfigs = getAllElementsConfig();
        Map<Long, SeaElementConfig> mapElementConfig = new HashMap<>();
        for (SeaElementConfig e : elementsConfigs) {
            mapElementConfig.put(e.getId(), e);
        }
        return mapElementConfig;
    }

    @Cacheable(cacheNames = "SeaElementConfig", key = "'SHIP'")
    public ShipElementConfig findShipElementConfig() {
        return seaElementConfigRepository.findShipElementConfig().orElseThrow(() -> KOSException.of(ErrorCode.CONFIG_NOT_FOUND));
    }
}
