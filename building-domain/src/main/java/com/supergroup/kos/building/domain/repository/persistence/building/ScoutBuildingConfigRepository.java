package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.config.ScoutBuildingConfig;

@Repository("ScoutBuildingConfigRepositoryJpa")
public interface ScoutBuildingConfigRepository extends BaseJpaRepository<ScoutBuildingConfig> {
    @Query("select s from ScoutBuildingConfig s where s.level = ?1")
    Optional<ScoutBuildingConfig> findByLevel(Long level);
    boolean existsByLevel(@NonNull Long level);
}
