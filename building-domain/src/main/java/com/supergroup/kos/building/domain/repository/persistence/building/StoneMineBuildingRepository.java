package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.building.StoneMineBuilding;

@Repository("StoneMineBuildingRepositoryJpa")
public interface StoneMineBuildingRepository extends BaseJpaRepository<StoneMineBuilding> {

    @Query("select w from StoneMineBuilding w where w.kosProfile.id = ?1")
    Optional<StoneMineBuilding> findByKosProfileId(Long id);

    @Query("select (count(a) > 0) from StoneMineBuilding a where a.kosProfile.id = ?1")
    boolean existsByKosProfileId(Long id);
}
