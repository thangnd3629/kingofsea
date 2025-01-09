package com.supergroup.kos.building.domain.model.mining;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
abstract public class MiningSnapshot {
    private LocalDateTime lastTimeClaim;
}
