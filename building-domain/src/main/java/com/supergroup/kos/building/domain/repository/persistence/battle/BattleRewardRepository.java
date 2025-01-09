package com.supergroup.kos.building.domain.repository.persistence.battle;

import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.battle.BattleReward;

@Repository
public interface BattleRewardRepository extends BaseJpaRepository<BattleReward> {
}
