package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.building.CastleBuilding;

@Repository("CastleBuildingRepositoryJpa")
public interface CastleBuildingRepository extends BaseJpaRepository<CastleBuilding> {

    Optional<CastleBuilding> findByKosProfile_Id(Long id);

    @Query("select (count(c) > 0) from CastleBuilding c where c.kosProfile.id = ?1")
    boolean existsByKosProfileId(Long id);

}
