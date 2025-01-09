package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.config.LighthouseBuildingConfig;

public interface LighthouseBuildingConfigRepository extends BaseJpaRepository<LighthouseBuildingConfig> {
    @Query("select r from LighthouseBuildingConfig r where r.level = ?1")
    Optional<LighthouseBuildingConfig> findByLevel(Long level);
}
