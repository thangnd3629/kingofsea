package com.supergroup.kos.building.domain.service.battle;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.model.battle.BattleReward;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRewardRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Service
@RequiredArgsConstructor
public class BattleRewardService {

    @Delegate
    private final BattleRewardRepository battleRewardRepository;

    public BattleReward save(BattleReward battleReward) {
        return battleRewardRepository.save(battleReward);
    }
}
