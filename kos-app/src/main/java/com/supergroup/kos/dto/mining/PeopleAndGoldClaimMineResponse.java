package com.supergroup.kos.dto.mining;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class PeopleAndGoldClaimMineResponse {
    private final Long          amountPeople;
    private final Long          amountGold;
    private final Long          totalPeople;
    private final Long          totalGold;
    private final LocalDateTime latestSnapshotDate;
}
