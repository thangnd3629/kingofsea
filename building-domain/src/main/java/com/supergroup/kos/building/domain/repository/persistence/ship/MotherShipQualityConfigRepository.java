package com.supergroup.kos.building.domain.repository.persistence.ship;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.constant.MotherShipQualityKey;
import com.supergroup.kos.building.domain.model.config.MotherShipQualityConfig;

@Repository("MotherShipQualityConfigRepositoryJpa")
public interface MotherShipQualityConfigRepository extends BaseJpaRepository<MotherShipQualityConfig> {
    List<MotherShipQualityConfig> findByOrderByQualityAsc();

    Optional<MotherShipQualityConfig> findByQuality(MotherShipQualityKey quality);

}
