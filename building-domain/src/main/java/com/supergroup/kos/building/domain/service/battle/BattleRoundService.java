package com.supergroup.kos.building.domain.service.battle;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRoundRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BattleRoundService {
    private final BattleRoundRepository battleRoundRepository;
}
