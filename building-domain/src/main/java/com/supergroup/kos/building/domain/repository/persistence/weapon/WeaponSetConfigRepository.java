package com.supergroup.kos.building.domain.repository.persistence.weapon;

import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.config.WeaponSetConfig;

@Repository("WeaponSetConfigRepositoryJpa")
public interface WeaponSetConfigRepository extends BaseJpaRepository<WeaponSetConfig> {

}
