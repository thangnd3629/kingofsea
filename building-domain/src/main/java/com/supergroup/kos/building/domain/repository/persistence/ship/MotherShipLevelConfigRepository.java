package com.supergroup.kos.building.domain.repository.persistence.ship;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.config.MotherShipLevelConfig;

@Repository("MotherShipLevelConfigRepositoryJpa")
public interface MotherShipLevelConfigRepository extends BaseJpaRepository<MotherShipLevelConfig> {
    List<MotherShipLevelConfig> findByOrderByLevelAsc();

    Optional<MotherShipLevelConfig> findByLevel(@NonNull Long level);

}
