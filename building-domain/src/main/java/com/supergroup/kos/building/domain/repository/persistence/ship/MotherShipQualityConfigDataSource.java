package com.supergroup.kos.building.domain.repository.persistence.ship;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.MotherShipQualityKey;
import com.supergroup.kos.building.domain.model.config.MotherShipQualityConfig;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MotherShipQualityConfigDataSource {

    private final MotherShipQualityConfigRepository motherShipQualityConfigRepository;

    @Cacheable(cacheNames = "MotherShipQualityConfig", key = "'LIST'")
    public List<MotherShipQualityConfig> getAll() {
        return motherShipQualityConfigRepository.findByOrderByQualityAsc();
    }

    @Cacheable(cacheNames = "MotherShipQualityConfig", key = "#quality.name()")
    public MotherShipQualityConfig getByQuality(MotherShipQualityKey quality) {
        return motherShipQualityConfigRepository.findByQuality(quality)
                                                .orElseThrow(() -> KOSException.of(ErrorCode.MOTHER_SHIP_QUALITY_CONFIG_IS_NOT_FOUND));
    }

    @Cacheable(cacheNames = "MotherShipQualityConfig", key = "'ByCommandBuildingConfig:' + #id.toString()")
    public List<MotherShipQualityConfig> getByCommandBuildingConfigId(Long id) {
        return getAll().stream().filter(config -> {
            var commandBuildingConfig = config.getCommandBuildingConfig();
            if (Objects.nonNull(commandBuildingConfig)) {
                return commandBuildingConfig.getId().equals(id);
            }
            return false;
        }).collect(Collectors.toList());
    }

}
