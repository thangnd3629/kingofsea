package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.mining.QueenBuilding;

@Repository("QueenBuildingRepositoryJpa")
public interface QueenBuildingRepository extends BaseJpaRepository<QueenBuilding> {

    @Query("select r from QueenBuilding r where r.kosProfile.id = ?1")
    Optional<QueenBuilding> findByKosProfileId(Long id);

    @Query("select (count(q) > 0) from QueenBuilding q where q.kosProfile.id = ?1")
    boolean existsByKosProfileId(Long id);

    @Transactional
    @Modifying
    @Query("update QueenBuilding q set q.numberOfQueenCard = ?1 where q.id = ?2")
    void updateNumberOfQueenCardById(@NonNull Long numberOfQueenCard, @NonNull Long id);

}
