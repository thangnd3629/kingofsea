package com.supergroup.kos.building.domain.service.mining;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.model.mining.StoneMiningResult;
import com.supergroup.kos.building.domain.model.mining.StoneMiningSnapshot;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoneMiningService extends MiningService<StoneMiningSnapshot, StoneMiningResult> {
    @Override
    public StoneMiningResult calculateMiningClaim(StoneMiningSnapshot latestSnapshot, LocalDateTime now) {

        var res = new StoneMiningResult();

        if (Objects.nonNull(latestSnapshot.getLastTimeClaim())
            && Objects.nonNull(latestSnapshot.getWorker())
            && Objects.nonNull(latestSnapshot.getStonePerWorker())) {
            var diff = ChronoUnit.SECONDS.between(now, latestSnapshot.getLastTimeClaim());
            var increase = Math.abs(diff) * latestSnapshot.getWorker() * latestSnapshot.getStonePerWorker();
            increase = (latestSnapshot.getStone() + increase) <= latestSnapshot.getCapacity() ? increase
                                                                                              :
                       (latestSnapshot.getCapacity() - latestSnapshot.getStone());
            res.setIncrease(increase);
        } else {
            res.setIncrease(0.0);
        }

        res.setLastTimeClaim(now);
        return res;
    }
}
