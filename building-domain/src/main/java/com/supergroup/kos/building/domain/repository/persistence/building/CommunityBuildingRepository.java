package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.building.CommunityBuilding;

@Repository("CommunityBuildingRepositoryJpa")
public interface CommunityBuildingRepository extends BaseJpaRepository<CommunityBuilding> {

    @Query("select r from CommunityBuilding r where r.kosProfile.id = ?1")
    Optional<CommunityBuilding> findByKosProfileId(Long id);

    @Query("select (count(c) > 0) from CommunityBuilding c where c.kosProfile.id = ?1")
    boolean existsByKosProfileId(Long id);
}
