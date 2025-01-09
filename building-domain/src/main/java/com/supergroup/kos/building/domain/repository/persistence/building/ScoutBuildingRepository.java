package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.mining.ScoutBuilding;

@Repository("ScoutBuildingRepositoryJpa")
public interface ScoutBuildingRepository extends BaseJpaRepository<ScoutBuilding> {
    Optional<ScoutBuilding> findByKosProfile_Id(Long id);

    boolean existsByKosProfile_Id(Long id);
    boolean existsByLevel(@NonNull Long level);

}
