package com.supergroup.kos.building.domain.repository.persistence.queen;

import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.config.QueenConfig;

@Repository("QueenConfigRepositoryJpa")
public interface QueenConfigRepository extends BaseJpaRepository<QueenConfig> {

}
