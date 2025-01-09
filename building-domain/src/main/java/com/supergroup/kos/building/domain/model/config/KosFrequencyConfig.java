package com.supergroup.kos.building.domain.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class KosFrequencyConfig {
    private Long frequencyGold;
    private Long frequencyWood;
    private Long frequencyStone;
    private Long frequencyPeople;
}
