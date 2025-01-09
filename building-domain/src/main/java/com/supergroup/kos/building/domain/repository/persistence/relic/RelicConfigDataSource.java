package com.supergroup.kos.building.domain.repository.persistence.relic;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.model.config.RelicConfig;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RelicConfigDataSource {

    private final RelicConfigRepository relicConfigRepository;

    @Cacheable(cacheNames = "RelicConfig", key = "'List'")
    public List<RelicConfig> getAll() {
        return relicConfigRepository.findAll();
    }

    @Cacheable(cacheNames = "RelicConfig", key = "#id")
    public RelicConfig getById(Long id) {
        RelicConfig config;
        config = relicConfigRepository.findById(id)
                                      .orElseThrow(() -> KOSException.of(ErrorCode.RELIC_MODEL_NOT_FOUND));
        return config;
    }

}
