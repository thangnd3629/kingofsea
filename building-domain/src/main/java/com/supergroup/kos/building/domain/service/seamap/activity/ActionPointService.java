package com.supergroup.kos.building.domain.service.seamap.activity;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaActivityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActionPointService {

    private final SeaActivityRepository repository;

    public Long getUsedActionPoint(Long kosProfileId) {
        return countActiveActivities(kosProfileId);
    }

    public Long countActiveActivities(Long kosProfileId) {
        return repository.countActiveActivities(kosProfileId);
    }
}
