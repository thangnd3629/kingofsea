package com.supergroup.kos.dto.mining;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ClaimMinePeopleAndGoldResponse {
    private Double        increasePeople;
    private Double        idlePeople;
    private Double        increaseGold;
    private Double        totalGold;
    private LocalDateTime latestSnapshotDate;
}
