package com.supergroup.kos.building.domain.model.asset;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
abstract public class AssetsAfterClaim {
    private LocalDateTime lastTimeClaim;
}
