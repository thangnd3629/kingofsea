package com.supergroup.kos.building.domain.repository.persistence.seamap;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.seamap.EscortShipSquad;

public interface EscortShipSquadRepository extends BaseJpaRepository<EscortShipSquad> {
    List<EscortShipSquad> findByIdIn(Collection<Long> ids);

    @Query("select e from EscortShipSquad e where e.escortShip.id = ?1 and e.lineUp.id = ?2")
    Optional<EscortShipSquad> findByEscortShipIdAndLineUpId(Long id, Long id1);
    
}
