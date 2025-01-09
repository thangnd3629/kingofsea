package com.supergroup.kos.building.domain.model.mining;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PeopleAndGoldMiningResult extends MiningResult {
    private Double increasePeople;
    private Double increaseGold;
    private Double totalGold;
    private Double totalPeople;
}
