package com.supergroup.kos.building.domain.service.battle;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.model.battle.BattleRoundSnapshot;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRoundSnapshotRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BattleRoundSnapShotService {
    private final BattleRoundSnapshotRepository repository;


    public Optional<BattleRoundSnapshot> getByBattleIdAndRound(Long battleId, Long round) {
        return repository.findByBattleRound_Battle_IdAndCurrentRound(battleId, round);
    }
}
