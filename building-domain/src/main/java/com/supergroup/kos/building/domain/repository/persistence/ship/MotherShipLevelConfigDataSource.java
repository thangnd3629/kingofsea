package com.supergroup.kos.building.domain.repository.persistence.ship;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.model.config.MotherShipLevelConfig;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MotherShipLevelConfigDataSource {

    private final MotherShipLevelConfigRepository      motherShipLevelConfigRepository;

    @Cacheable(cacheNames = "MotherShipLevelConfig", key = "'LIST'")
    public List<MotherShipLevelConfig> getAll() {
        return motherShipLevelConfigRepository.findByOrderByLevelAsc();
    }

    @Cacheable(cacheNames = "MotherShipLevelConfig", key = "#level.toString()")
    public MotherShipLevelConfig getByLevel(Long level) {
        return motherShipLevelConfigRepository.findByLevel(level)
                                              .orElseThrow(() -> KOSException.of(ErrorCode.MOTHER_SHIP_LEVEL_CONFIG_IS_NOT_FOUND));
    }

    @Cacheable(cacheNames = "MotherShipLevelConfig", key = "'ByCommandBuildingConfig:' + #id.toString()")
    public List<MotherShipLevelConfig> getByCommandBuildingConfigId(Long id) {
        return getAll().stream().filter(config -> {
            var commandBuildingConfig = config.getCommandBuildingConfig();
            if (Objects.nonNull(commandBuildingConfig)) {
                return commandBuildingConfig.getId().equals(id);
            }
            return false;
        }).collect(Collectors.toList());
    }

}
