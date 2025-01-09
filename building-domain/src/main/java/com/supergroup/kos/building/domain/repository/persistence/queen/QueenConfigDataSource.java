package com.supergroup.kos.building.domain.repository.persistence.queen;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.model.config.QueenConfig;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QueenConfigDataSource {

    private final QueenConfigRepository queenConfigRepository;

    @Cacheable(cacheNames = "QueenConfig", key = "'List'")
    public List<QueenConfig> getAll() {
        return queenConfigRepository.findAll();
    }

    @Cacheable(cacheNames = "QueenConfig", key = "#id")
    public QueenConfig getById(Long id) {
        return queenConfigRepository.findById(id)
                                    .orElseThrow(() -> KOSException.of(ErrorCode.QUEEN_MODEL_NOT_FOUND));
    }

}
