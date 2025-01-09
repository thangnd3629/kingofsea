package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.building.CommandBuilding;

@Repository("CommandBuildingRepositoryJpa")
public interface CommandBuildingRepository extends BaseJpaRepository<CommandBuilding> {
    Optional<CommandBuilding> findByKosProfile_Id(Long id);

    @Query("select c from CommandBuilding c where c.kosProfile.id = ?1")
    Optional<CommandBuilding> findByKosProfileId(Long id);

    @Query("select (count(c) > 0) from CommandBuilding c where c.kosProfile.id = ?1")
    boolean existsByKosProfileId(Long id);
    boolean existsByLevel(@NonNull Long level);
}
