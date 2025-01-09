package com.supergroup.kos.dto.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class KosFrequencyConfigDTO {
    private Long frequencyGold;
    private Long frequencyWood;
    private Long frequencyStone;
    private Long frequencyPeople;
}
