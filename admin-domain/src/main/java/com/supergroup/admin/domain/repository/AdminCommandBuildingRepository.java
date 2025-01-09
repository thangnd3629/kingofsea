package com.supergroup.admin.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.building.CommandBuilding;

@Repository
public interface AdminCommandBuildingRepository extends BaseJpaRepository<CommandBuilding> {

    @Query("select c from CommandBuilding c where c.kosProfile.id = ?1")
    Optional<CommandBuilding> findByKosProfileId(@NonNull Long id);

}
