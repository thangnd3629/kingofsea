package com.supergroup.kos.building.domain.model.mining;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class WoodMiningSnapshot extends MiningSnapshot {
    private LocalDateTime lastTimeClaim;
    private Long          worker;
    private Double        woodPerWorker;
    private Double        wood;
    private Long          capacity;
}
