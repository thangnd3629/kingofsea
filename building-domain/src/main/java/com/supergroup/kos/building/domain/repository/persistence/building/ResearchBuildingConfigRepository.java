package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.config.ResearchBuildingConfig;

@Repository("ResearchBuildingConfigRepositoryJpa")
public interface ResearchBuildingConfigRepository extends BaseJpaRepository<ResearchBuildingConfig> {

    @Query("select r from ResearchBuildingConfig r where r.level = ?1")
    Optional<ResearchBuildingConfig> findByLevel(Long level);
    boolean existsByLevel(@NonNull Long level);

}
