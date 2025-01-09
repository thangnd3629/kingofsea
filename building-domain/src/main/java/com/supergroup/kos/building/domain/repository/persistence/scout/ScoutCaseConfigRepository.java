package com.supergroup.kos.building.domain.repository.persistence.scout;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.config.ScoutCaseConfig;

@Repository("ScoutCaseConfigRepositoryJpa")
public interface ScoutCaseConfigRepository extends BaseJpaRepository<ScoutCaseConfig> {
    List<ScoutCaseConfig> findByNumberEnemy(Long enemy);

}
