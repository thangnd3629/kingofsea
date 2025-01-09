package com.supergroup.kos.building.domain.service.battle;

import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.model.config.battle.BattleFieldConfig;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleFieldConfigRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Service
@RequiredArgsConstructor
public class BattleFieldConfigService {
    @Delegate
    private final BattleFieldConfigRepository repository;


    public BattleFieldConfig chooseBattleFieldByThresh(Integer thresh) {
        return repository.findFirstByMinShipLessThanOrderByMinShipDesc(thresh).orElseThrow(()-> KOSException.of(ErrorCode.CONFIG_NOT_FOUND));
    }
}
