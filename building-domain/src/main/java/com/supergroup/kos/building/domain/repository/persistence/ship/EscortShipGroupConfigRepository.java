package com.supergroup.kos.building.domain.repository.persistence.ship;

import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.config.EscortShipGroupConfig;

@Repository("EscortShipGroupConfigRepositoryJpa")
public interface EscortShipGroupConfigRepository extends BaseJpaRepository<EscortShipGroupConfig> {

}
