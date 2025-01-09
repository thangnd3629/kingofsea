package com.supergroup.kos.dto.building;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CastleConfigReward {
    private Long   level;
    private Long   maxPopulation;
    private Double populationGrowthBase;
    private Double goldPerPerson;
    private Long   gpGain;

}
