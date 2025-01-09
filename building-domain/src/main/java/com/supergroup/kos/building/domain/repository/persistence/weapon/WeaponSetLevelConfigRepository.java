package com.supergroup.kos.building.domain.repository.persistence.weapon;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.constant.WeaponSetLevel;
import com.supergroup.kos.building.domain.model.config.WeaponSetLevelConfig;

@Repository("WeaponSetLevelConfigRepositoryJpa")
public interface WeaponSetLevelConfigRepository extends BaseJpaRepository<WeaponSetLevelConfig> {

    Optional<WeaponSetLevelConfig> findByLevel(@NonNull WeaponSetLevel level);

    @Query("select w from WeaponSetLevelConfig w where w.armoryBuildingConfig.id = ?1")
    List<WeaponSetLevelConfig> findByArmoryBuildingConfigId(@NonNull Long id);

    List<WeaponSetLevelConfig> findByOrderByLevelAsc();


}
