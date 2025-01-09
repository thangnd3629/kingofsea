package com.supergroup.admin.domain.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.seamap.SeaMapRefreshTransaction;

@Repository("AdminSeaMapRefreshTransactionRepositoryJpa")
public interface AdminSeaMapRefreshTransactionRepository extends BaseJpaRepository<SeaMapRefreshTransaction> {
    Optional<SeaMapRefreshTransaction> findFirstByOrderByIdDesc();
}
