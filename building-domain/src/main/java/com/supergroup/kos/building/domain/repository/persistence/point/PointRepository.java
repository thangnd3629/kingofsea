package com.supergroup.kos.building.domain.repository.persistence.point;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.point.Point;

@Repository("PointRepositoryJpa")
public interface PointRepository extends BaseJpaRepository<Point> {

    Optional<Point> findByKosProfile_Id(Long id);

    @Query("select (count(p) > 0) from Point p where p.kosProfile.id = ?1")
    boolean existsByKosProfileId(Long id);

    @Transactional
    @Modifying
    @Query("update Point p set p.mpPoint = ?1 where p.kosProfile.id = ?2")
    void updateMpPointByKosProfileId(@NonNull Long mpPoint, @NonNull Long kosProfileId);


}
