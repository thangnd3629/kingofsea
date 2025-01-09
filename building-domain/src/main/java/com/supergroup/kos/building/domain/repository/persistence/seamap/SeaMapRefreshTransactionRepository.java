package com.supergroup.kos.building.domain.repository.persistence.seamap;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.seamap.SeaMapRefreshTransaction;

@Repository("SeaMapRefreshTransactionRepositoryJpa")
public interface SeaMapRefreshTransactionRepository extends BaseJpaRepository<SeaMapRefreshTransaction> {
    Optional<SeaMapRefreshTransaction> findFirstByOrderByIdDesc();
}
