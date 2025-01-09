package com.supergroup.kos.building.domain.service.ship;

import java.util.List;

import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.model.config.MotherShipLevelConfig;
import com.supergroup.kos.building.domain.repository.persistence.ship.MotherShipLevelConfigRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MotherShipLevelConfigService {

    private final MotherShipLevelConfigRepository motherShipLevelConfigRepository;

    public List<MotherShipLevelConfig> getConfigs() {
        return motherShipLevelConfigRepository.findByOrderByLevelAsc();
    }

    public MotherShipLevelConfig getConfigByLevel(Long level) {
        return motherShipLevelConfigRepository.findByLevel(level)
                                              .orElseThrow(() -> KOSException.of(ErrorCode.MOTHER_SHIP_LEVEL_CONFIG_IS_NOT_FOUND));
    }
}
