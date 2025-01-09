package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.building.LighthouseBuilding;

public interface LighthouseBuildingRepository extends BaseJpaRepository<LighthouseBuilding> {
    @Query("select v from LighthouseBuilding v where v.kosProfile.id = ?1")
    Optional<LighthouseBuilding> findByKosProfileId(Long id);
    boolean existsByLevel(@NonNull Long level);
}
