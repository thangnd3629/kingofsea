package com.supergroup.kos.building.domain.repository.persistence.ship;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.constant.EscortShipGroupName;
import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.model.ship.EscortShip;

@Repository("EscortShipRepositoryJpa")
public interface EscortShipRepository extends BaseJpaRepository<EscortShip> {
    @Query("select e from EscortShip e " +
           "where e.escortShipGroup.assets.kosProfile.id = ?1 and e.escortShipConfig.type = ?2")
    Optional<EscortShip> findByKosProfileIdAndShipType(@NonNull Long id,
                                                       @NonNull EscortShipType type);

    @Query("select e from EscortShip e " +
           "where e.escortShipGroup.assets.kosProfile.id = ?1 and e.escortShipGroup.escortShipGroupLevelConfig.escortShipGroupConfig.name = ?2")
    List<EscortShip> findEscortShipsGroupShip(
            @NonNull Long kosProfileId, @NonNull EscortShipGroupName name);

    @Query("select e from EscortShip e " +
           "where e.escortShipGroup.assets.kosProfile.id = ?1 order by e.escortShipConfig.id")
    List<EscortShip> findByKosProfileId(@NonNull Long id);

    @Query("select e from EscortShip e " +
           "where e.escortShipGroup.assets.kosProfile.id = ?1 and e.inBuildQueue = true " +
           "order by e.startQueueTime")
    List<EscortShip> findByEscortShipQueueing(@NonNull Long kosProfileId);

    @Query("select e from EscortShip e " +
           "where (e.buildSession is not null or e.inBuildQueue = true) and e.escortShipGroup.assets.kosProfile.id = ?1 "
           + "order by e.buildSession.id, e.startQueueTime")
    List<EscortShip> findByEscortShipBuildingOrQueueing(@NonNull Long kosProfileId);

}
