package com.supergroup.kos.building.domain.repository.persistence.ship;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.constant.EscortShipGroupName;
import com.supergroup.kos.building.domain.model.ship.EscortShipGroup;

@Repository("EscortShipGroupRepositoryJpa")
public interface EscortShipGroupRepository extends BaseJpaRepository<EscortShipGroup> {

    @Query("select e from EscortShipGroup e where e.assets.kosProfile.id = ?1 ")
    List<EscortShipGroup> findByKosProfileId(@NonNull Long id);

    @Query("select e from EscortShipGroup e " +
           "where e.assets.kosProfile.id = ?1 " +
           "order by e.escortShipGroupLevelConfig.escortShipGroupConfig.name")
    List<EscortShipGroup> findByKosProfileIdOrderByEscortShipGroupNameAsc(Long id);


    @Query("select e from EscortShipGroup e " +
           "where e.assets.kosProfile.id = ?1 and e.escortShipGroupLevelConfig.escortShipGroupConfig.name = ?2")
    Optional<EscortShipGroup> findKosProfileIdAndEscortShipGroupConfigName(@NonNull Long id, @NonNull
    EscortShipGroupName name);

    @Query("select (count(e) > 0) from EscortShipGroup e where e.assets.kosProfile.id = ?1")
    boolean existsByKosProfileId(@NonNull Long id);



}
