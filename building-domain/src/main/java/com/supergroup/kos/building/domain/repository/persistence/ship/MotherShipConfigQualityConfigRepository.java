package com.supergroup.kos.building.domain.repository.persistence.ship;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.config.MotherShipConfigQualityConfig;

@Repository("MotherShipConfigQualityConfigRepositoryJpa")
public interface MotherShipConfigQualityConfigRepository extends BaseJpaRepository<MotherShipConfigQualityConfig> {
    @Query("select m from MotherShipConfigQualityConfig m order by m.motherShipConfig.id, m.motherShipQualityConfig.quality")
    List<MotherShipConfigQualityConfig> findByOrderByMotherShipConfigIdAndQualityAsc();

    @Query("select m from MotherShipConfigQualityConfig m " +
           "where m.motherShipConfig.id = ?1 and m.motherShipQualityConfig.id = ?2")
    Optional<MotherShipConfigQualityConfig> findByModelIdAndQualityId(@NonNull Long id, @NonNull Long id1);

}
