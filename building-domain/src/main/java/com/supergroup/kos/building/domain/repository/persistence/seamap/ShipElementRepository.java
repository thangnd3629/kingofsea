package com.supergroup.kos.building.domain.repository.persistence.seamap;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.seamap.ShipElement;

public interface ShipElementRepository extends BaseJpaRepository<ShipElement> {
    @Query(value = "select se from ShipElement se where se.active is true")
    List<ShipElement> findActiveShipElement();
}
