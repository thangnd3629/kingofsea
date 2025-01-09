package com.supergroup.kos.building.domain.repository.persistence.ship;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.model.config.EscortShipLevelConfig;

@Repository("EscortShipLevelConfigRepositoryJpa")
public interface EscortShipLevelConfigRepository extends BaseJpaRepository<EscortShipLevelConfig> {
    Optional<EscortShipLevelConfig> findByTypeAndLevel(@NonNull EscortShipType type, @NonNull Long level);


    @Query("select e from EscortShipLevelConfig e where e.type = ?1 order by e.level")
    List<EscortShipLevelConfig> findByTypeOrderByLevelAsc(@NonNull EscortShipType type);



}
