package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.building.MilitaryBuilding;

@Repository("MilitaryBuildingRepositoryJpa")
public interface MilitaryBuildingRepository extends BaseJpaRepository<MilitaryBuilding> {
    Optional<MilitaryBuilding> findByKosProfile_Id(Long id);

    @Query("select m from MilitaryBuilding m where m.kosProfile.id = ?1")
    Optional<MilitaryBuilding> findByKosProfileId(Long id);

    @Query("select (count(c) > 0) from MilitaryBuilding c where c.kosProfile.id = ?1")
    boolean existsByKosProfileId(Long id);

    boolean existsByLevel(@NonNull Long level);

}
