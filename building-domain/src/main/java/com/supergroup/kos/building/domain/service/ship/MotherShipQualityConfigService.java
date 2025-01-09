package com.supergroup.kos.building.domain.service.ship;

import java.util.List;

import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.MotherShipQualityKey;
import com.supergroup.kos.building.domain.model.config.MotherShipQualityConfig;
import com.supergroup.kos.building.domain.repository.persistence.ship.MotherShipQualityConfigRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MotherShipQualityConfigService {

    private final MotherShipQualityConfigRepository motherShipQualityConfigRepository;

    public List<MotherShipQualityConfig> getConfigs() {
        return motherShipQualityConfigRepository.findByOrderByQualityAsc();
    }

    public MotherShipQualityConfig getConfigByQuality(MotherShipQualityKey quality) {
        return motherShipQualityConfigRepository.findByQuality(quality)
                                                .orElseThrow(() -> KOSException.of(ErrorCode.MOTHER_SHIP_QUALITY_CONFIG_IS_NOT_FOUND));
    }
}
