package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.building.WoodMineBuilding;

@Repository("WoodMineBuildingRepositoryJpa")
public interface WoodMineBuildingRepository extends BaseJpaRepository<WoodMineBuilding> {

    @Query("select w from WoodMineBuilding w where w.kosProfile.id = ?1")
    Optional<WoodMineBuilding> findByKosProfileId(Long id);

    @Query("select (count(a) > 0) from WoodMineBuilding a where a.kosProfile.id = ?1")
    boolean existsByKosProfileId(Long id);
}
