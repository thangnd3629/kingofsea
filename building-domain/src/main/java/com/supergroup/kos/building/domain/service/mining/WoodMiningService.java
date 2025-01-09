package com.supergroup.kos.building.domain.service.mining;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.model.mining.WoodMiningResult;
import com.supergroup.kos.building.domain.model.mining.WoodMiningSnapshot;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WoodMiningService extends MiningService<WoodMiningSnapshot, WoodMiningResult> {
    @Override
    WoodMiningResult calculateMiningClaim(WoodMiningSnapshot latestSnapshot, LocalDateTime now) {

        var res = new WoodMiningResult();

        if (Objects.nonNull(latestSnapshot.getLastTimeClaim())
            && Objects.nonNull(latestSnapshot.getWorker())
            && Objects.nonNull(latestSnapshot.getWoodPerWorker())) {
            var diff = ChronoUnit.SECONDS.between(now, latestSnapshot.getLastTimeClaim());
            var increase = Math.abs(diff) * latestSnapshot.getWorker() * latestSnapshot.getWoodPerWorker();
            increase = (latestSnapshot.getWood() + increase) <= latestSnapshot.getCapacity() ? increase :
                       (latestSnapshot.getCapacity() - latestSnapshot.getWood());
            res.setIncrease(increase);
        } else {
            res.setIncrease(0.0);
        }
        res.setLastTimeClaim(now);
        return res;
    }

}