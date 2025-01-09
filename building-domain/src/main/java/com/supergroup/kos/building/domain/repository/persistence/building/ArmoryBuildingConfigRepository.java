
package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.config.ArmoryBuildingConfig;

@Repository("ArmoryBuildingConfigRepositoryJpa")
public interface ArmoryBuildingConfigRepository extends BaseJpaRepository<ArmoryBuildingConfig> {

    @Query("select c from ArmoryBuildingConfig c where c.level = ?1")
    Optional<ArmoryBuildingConfig> findByLevel(Long level);

    boolean existsByLevel(@NonNull Long level);

}
