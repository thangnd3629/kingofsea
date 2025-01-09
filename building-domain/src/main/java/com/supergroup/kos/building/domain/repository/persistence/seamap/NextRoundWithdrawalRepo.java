package com.supergroup.kos.building.domain.repository.persistence.seamap;

import java.util.List;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.battle.NextRoundWithdrawal;

public interface NextRoundWithdrawalRepo extends BaseJpaRepository<NextRoundWithdrawal> {
    List<NextRoundWithdrawal> findByBattleId(Long battleId);
    void deleteByBattleId(Long battleId);
}
