package com.supergroup.kos.dto.building;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PopulationCompositionResponse {
    private Long totalPeople;
    private Long idle;
    private Long mason;
    private Long carpenter;
    private Long maxPeople;
}
