package com.supergroup.kos.building.domain.repository.persistence.upgrade;

import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;

@Repository("UpgradeSessionRepositoryJpa")
public interface UpgradeSessionRepository extends BaseJpaRepository<UpgradeSession> {
}
