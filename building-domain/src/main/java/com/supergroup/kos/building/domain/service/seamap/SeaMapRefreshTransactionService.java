package com.supergroup.kos.building.domain.service.seamap;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.model.seamap.SeaMapRefreshTransaction;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaMapRefreshTransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Service
@RequiredArgsConstructor
public class SeaMapRefreshTransactionService {
    @Delegate
    private final SeaMapRefreshTransactionRepository seaMapRefreshTransactionRepository;

    public Optional<SeaMapRefreshTransaction> findLastRefreshTransaction() {
        return seaMapRefreshTransactionRepository.findFirstByOrderByIdDesc();
    }

    public SeaMapRefreshTransaction save(SeaMapRefreshTransaction seaMapRefreshTransaction) {
        return seaMapRefreshTransactionRepository.save(seaMapRefreshTransaction);
    }

}
