package com.supergroup.kos.building.domain.repository.persistence.seamap;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.config.seamap.BossSeaConfig;
import com.supergroup.kos.building.domain.model.config.seamap.ResourceIslandConfig;
import com.supergroup.kos.building.domain.model.config.seamap.SeaElementConfig;
import com.supergroup.kos.building.domain.model.config.seamap.ShipElementConfig;

@Repository
public interface SeaElementConfigRepository<T extends SeaElementConfig> extends BaseJpaRepository<T> {
    @Query("select e from SeaElementConfig e ")
    List<SeaElementConfig> getAllElementsConfig();

    @Query("select se from ShipElementConfig se")
    Optional<ShipElementConfig> findShipElementConfig();
    @Query("select re from ResourceIslandConfig re where re.id = :id")
    Optional<ResourceIslandConfig> findResourceIslandConfigById(Long id);

    @Query("select bs from BossSeaConfig bs where bs.id = :id")
    Optional<BossSeaConfig> findBossSeaConfigById(Long id);
}
