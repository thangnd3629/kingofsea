package com.supergroup.kos.building.domain.repository.persistence.battle;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.battle.BattleRound;

@Repository
public interface BattleRoundRepository extends BaseJpaRepository<BattleRound> {
    Optional<BattleRound> findFirstByOrderByIdDesc();

    Optional<BattleRound> findByBattle_IdAndIndex(Long id, Long index);

    @Query("SELECT rr.round.id FROM RoundReport rr WHERE rr.round.battle.id = ?1 ORDER BY rr.round.id")
    List<Long> roundIdsOfBattle(Long battleId);
}
