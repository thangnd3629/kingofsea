package com.supergroup.kos.building.domain.repository.persistence.queen;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.queen.Queen;

@Repository("QueenRepositoryJpa")
public interface QueenRepository extends BaseJpaRepository<Queen> {
    List<Queen> findByQueenBuilding_KosProfile_Id(@NonNull Long id);

    @Query("select q from Queen q where q.id = ?1 and q.queenBuilding.kosProfile.id = ?2")
    Optional<Queen> findByIdAndQueenBuildingKosProfileId(@NonNull Long queenId, @NonNull Long kosProfileId);

    @Transactional
    @Modifying
    @Query("delete from Queen q where q.queenBuilding.id = ?1")
    void deleteByQueenBuildingId(Long queenBuildingId);

}