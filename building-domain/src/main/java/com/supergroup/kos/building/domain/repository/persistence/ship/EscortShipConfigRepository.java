package com.supergroup.kos.building.domain.repository.persistence.ship;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.model.config.EscortShipConfig;

@Repository("EscortShipConfigRepositoryJpa")
public interface EscortShipConfigRepository extends BaseJpaRepository<EscortShipConfig> {
    List<EscortShipConfig> findByMilitaryLevelRequiredOrderByIdAsc(@NonNull Long militaryLevelRequired);

    Optional<EscortShipConfig> findByType(@NonNull EscortShipType type);

    List<EscortShipConfig> findByOrderByIdAsc();


}
