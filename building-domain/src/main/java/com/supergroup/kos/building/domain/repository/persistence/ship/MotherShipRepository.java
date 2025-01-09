package com.supergroup.kos.building.domain.repository.persistence.ship;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.ship.MotherShip;

@Repository("MotherShipRepositoryJpa")
public interface MotherShipRepository extends BaseJpaRepository<MotherShip> {

    @Query("select m from MotherShip m where m.commandBuilding.kosProfile.id = ?1")
    List<MotherShip> findByKosProfileId(@NonNull Long id);

    @Query("select m from MotherShip m where m.id = ?1 and m.commandBuilding.kosProfile.id = ?2")
    Optional<MotherShip> findByIdAndKosProfileId(@NonNull Long motherShipId, @NonNull Long kosProfileId);

    List<MotherShip> findByIdIn(Collection<Long> ids);

    @Modifying
    @Query("update MotherShip m set m.currentHp = ?2, m.arrivalMainBaseTime = ?3, m.lastTimeCalculateHp = ?4 where m.id = ?1")
    @Transactional
    Integer updateForRecoveryHp(Long id, Long currentHp, LocalDateTime arrivalMainBaseTime, LocalDateTime lastTimeCalculateHp);
}
