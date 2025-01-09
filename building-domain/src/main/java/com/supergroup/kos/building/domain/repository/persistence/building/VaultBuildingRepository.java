package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.building.VaultBuilding;

@Repository("VaultBuildingRepository")
public interface VaultBuildingRepository extends BaseJpaRepository<VaultBuilding> {

    @Query("select v from VaultBuilding v where v.kosProfile.id = ?1")
    Optional<VaultBuilding> findByKosProfileId(Long id);

    @Query("select (count(v) > 0) from VaultBuilding v where v.kosProfile.id = ?1")
    boolean existsByKosProfileId(Long id);

}
