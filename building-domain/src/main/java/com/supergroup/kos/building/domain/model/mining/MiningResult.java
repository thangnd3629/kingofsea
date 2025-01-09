package com.supergroup.kos.building.domain.model.mining;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
abstract public class MiningResult {
    private Double        increase;
    private LocalDateTime lastTimeClaim;
    private Double        total;
}
