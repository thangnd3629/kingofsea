package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.config.QueenBuildingConfig;

@Repository("QueenBuildingConfigRepositoryJpa")
public interface QueenBuildingConfigRepository extends BaseJpaRepository<QueenBuildingConfig> {
    @Query("select r from QueenBuildingConfig r where r.level = ?1")
    Optional<QueenBuildingConfig> findByLevel(Long level);
    boolean existsByLevel(@NonNull Long level);

}
