package com.supergroup.admin.domain.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.supergroup.admin.domain.repository.AdminSeaMapRefreshTransactionRepository;
import com.supergroup.kos.building.domain.model.seamap.SeaMapRefreshTransaction;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Service
@RequiredArgsConstructor
public class AdminSeaMapRefreshTransactionService {
    @Delegate
    private final AdminSeaMapRefreshTransactionRepository adminSeaMapRefreshTransactionRepository;

    public Optional<SeaMapRefreshTransaction> findLastRefreshTransaction() {
        return adminSeaMapRefreshTransactionRepository.findFirstByOrderByIdDesc();
    }

    public SeaMapRefreshTransaction save(SeaMapRefreshTransaction seaMapRefreshTransaction) {
        return adminSeaMapRefreshTransactionRepository.save(seaMapRefreshTransaction);
    }

}
