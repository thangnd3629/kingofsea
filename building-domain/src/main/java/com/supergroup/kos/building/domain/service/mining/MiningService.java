package com.supergroup.kos.building.domain.service.mining;

import java.time.LocalDateTime;

import com.supergroup.kos.building.domain.model.mining.MiningResult;
import com.supergroup.kos.building.domain.model.mining.MiningSnapshot;

public abstract class MiningService<T extends MiningSnapshot, R extends MiningResult> {

    public R getMiningClaim(T latestSnapshot) {
        return calculateMiningClaim(latestSnapshot, LocalDateTime.now());
    }

    /**
     * Get mining result from the latest snapshot to now
     * And snapshot all parameter in mining
     */
    abstract R calculateMiningClaim(T latestSnapshot, LocalDateTime now);

}
