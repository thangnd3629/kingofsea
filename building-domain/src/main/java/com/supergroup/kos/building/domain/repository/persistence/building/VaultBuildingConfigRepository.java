package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.config.VaultBuildingConfig;

@Repository("VaultBuildingConfigRepositoryJpa")
public interface VaultBuildingConfigRepository extends BaseJpaRepository<VaultBuildingConfig> {
    @Query("select v from VaultBuildingConfig v where v.level = ?1")
    Optional<VaultBuildingConfig> findByLevel(Long level);
    boolean existsByLevel(@NonNull Long level);

}

