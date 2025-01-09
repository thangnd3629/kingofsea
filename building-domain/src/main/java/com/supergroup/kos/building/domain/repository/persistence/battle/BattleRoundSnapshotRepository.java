package com.supergroup.kos.building.domain.repository.persistence.battle;

import java.util.Optional;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.battle.BattleRoundSnapshot;

public interface BattleRoundSnapshotRepository extends BaseJpaRepository<BattleRoundSnapshot> {
    Optional<BattleRoundSnapshot> findByBattleRound_Battle_IdAndCurrentRound(Long id, Long currentRound);

}
