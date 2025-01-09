package com.supergroup.kos.building.domain.repository.persistence.battle;

import java.util.Optional;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.config.battle.BattleFieldConfig;

public interface BattleFieldConfigRepository extends BaseJpaRepository<BattleFieldConfig> {
    Optional<BattleFieldConfig> findFirstByMinShipLessThanOrderByMinShipDesc(Integer minShip);
}
