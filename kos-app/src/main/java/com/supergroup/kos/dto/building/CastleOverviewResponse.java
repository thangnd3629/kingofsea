package com.supergroup.kos.dto.building;

import com.supergroup.kos.building.domain.constant.IslandStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CastleOverviewResponse {
    private Long         level;
    private Long         winStreak;
    private Long         mpPoint;
    private Long         gpPoint;
    private Long         tpPoint;
    private Double       peopleProduction;
    private Double       woodProduction;
    private Double       stoneProduction;
    private Double       goldProduction;
    private Long         totalPeople;
    private Long         idlePeople;
    private Long         maxPeople;
    private Long         gold;
    private IslandStatus islandStatus;
    private Double       mpMultiplier;
    private Long    maxActionPoints;
}
