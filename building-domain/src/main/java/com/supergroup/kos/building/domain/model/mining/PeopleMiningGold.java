package com.supergroup.kos.building.domain.model.mining;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PeopleMiningGold {
    private Long peopleMiningGold;
    private Double peopleDecimal;
    private Double gold;
}
