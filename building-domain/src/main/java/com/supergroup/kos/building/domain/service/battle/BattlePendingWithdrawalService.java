package com.supergroup.kos.building.domain.service.battle;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.kos.building.domain.model.battle.NextRoundWithdrawal;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.NextRoundWithdrawalRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BattlePendingWithdrawalService {
    private final BattleRepository battleRepository;
    private final NextRoundWithdrawalRepo nextRoundWithdrawalRepo;
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void enqueueWithdrawalTask(Long battleId, Long lineUpId){
        log.info("Can not withdraw, waiting for new round");
        NextRoundWithdrawal nextRoundWithdrawal = new NextRoundWithdrawal();
        nextRoundWithdrawal.setBattleId(battleId);
        nextRoundWithdrawal.setLineUpId(lineUpId);
        nextRoundWithdrawalRepo.save(nextRoundWithdrawal);
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void enqueueAttackerWithdrawalTask(Long battleId){
        battleRepository.setAttackerWithdrawAll(battleId);
    }

}
