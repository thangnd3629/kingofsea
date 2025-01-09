package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.building.ArmoryBuilding;

@Repository("ArmoryBuildingRepositoryJpa")
public interface ArmoryBuildingRepository extends BaseJpaRepository<ArmoryBuilding> {
    @Query("select r from ArmoryBuilding r where r.kosProfile.id = ?1")
    Optional<ArmoryBuilding> findByKosProfileId(Long id);

    @Query("select (count(c) > 0) from ArmoryBuilding c where c.kosProfile.id = ?1")
    boolean existsByKosProfileId(Long id);
}
