package com.supergroup.kos.building.domain.model.upgrade;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UpgradeResource {
    private final Long wood;
    private final Long stone;
    private final Long gold;
    private final Long upgradeDuration; // min
}
