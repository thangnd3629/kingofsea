package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.config.CommandBuildingConfig;

@Repository("CommandBuildingConfigRepositoryJpa")
public interface CommandBuildingConfigRepository extends BaseJpaRepository<CommandBuildingConfig> {

    @Query("select c from CommandBuildingConfig c where c.level = ?1")
    Optional<CommandBuildingConfig> findByLevel(Long level);
}
