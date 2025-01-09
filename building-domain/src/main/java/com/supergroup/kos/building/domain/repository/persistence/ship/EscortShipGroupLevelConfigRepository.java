package com.supergroup.kos.building.domain.repository.persistence.ship;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.constant.EscortShipGroupLevel;
import com.supergroup.kos.building.domain.constant.EscortShipGroupName;
import com.supergroup.kos.building.domain.model.config.EscortShipGroupLevelConfig;

@Repository("EscortShipGroupLevelConfigRepositoryJpa")
public interface EscortShipGroupLevelConfigRepository extends BaseJpaRepository<EscortShipGroupLevelConfig> {

    @Query("select e from EscortShipGroupLevelConfig e order by e.escortShipGroupConfig.name, e.level")
    List<EscortShipGroupLevelConfig> findByOrderByShipGroupConfigNameAscLevelAsc();

    @Query("select e from EscortShipGroupLevelConfig e where e.escortShipGroupConfig.name = ?1 and e.level = ?2")
    Optional<EscortShipGroupLevelConfig> findByGroupNameAndGroupLevel(@NonNull EscortShipGroupName name,
                                                                      @NonNull EscortShipGroupLevel level);

    @Query("select e from EscortShipGroupLevelConfig e where e.armoryBuildingConfig.id = ?1")
    List<EscortShipGroupLevelConfig> findByArmoryBuildingConfigId(@NonNull Long id);

}
