package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.mining.ResearchBuilding;

@Repository("ResearchBuildingRepositoryJpa")
public interface ResearchBuildingRepository extends BaseJpaRepository<ResearchBuilding> {

    @Query("select r from ResearchBuilding r where r.kosProfile.id = ?1")
    Optional<ResearchBuilding> findByKosProfileId(Long id);

    @Query("select (count(r) > 0) from ResearchBuilding r where r.kosProfile.id = ?1")
    boolean existsByKosProfileId(Long id);
}
