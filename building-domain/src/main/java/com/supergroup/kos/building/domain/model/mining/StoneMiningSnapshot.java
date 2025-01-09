package com.supergroup.kos.building.domain.model.mining;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class StoneMiningSnapshot extends MiningSnapshot {
    private Long   worker;
    private Double stonePerWorker;
    private Double stone;
    private Long   capacity;
}
