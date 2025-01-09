package com.supergroup.kos.building.domain.model.mining;

import java.time.LocalDateTime;

import com.supergroup.kos.building.domain.constant.TypeChangeMiningPeople;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MiningPeople {
    private Long                   mp;
    private Double                 firstPeople;
    private Long                   peopleChange;
    private Long                   level;
    private TypeChangeMiningPeople type;
    private LocalDateTime          timeInfluence;
}
