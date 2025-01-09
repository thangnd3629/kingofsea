package com.supergroup.kos.building.domain.repository.persistence.relic;

import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.config.RelicConfig;

@Repository("RelicConfigRepositoryJpa")
public interface RelicConfigRepository extends BaseJpaRepository<RelicConfig> {

}
