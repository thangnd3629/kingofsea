package com.supergroup.kos.building.domain.model.mining;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PeopleAndGoldMiningSnapshot extends MiningSnapshot {
    private Double idlePeople;
    private Long   mp;
    private Double goldPerPerson;
    private Double mpMultiplier;
    private Double populationGrowthBase;
    private Long   maxPopulation;
    private Long   peopleInWork;
}
