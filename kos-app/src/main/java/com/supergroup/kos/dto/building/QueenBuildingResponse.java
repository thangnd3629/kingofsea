package com.supergroup.kos.dto.building;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class QueenBuildingResponse {
    private Long level;
    private Long mpGained;
    private Long numberOfQueen;
    private Long numberOfQueenCard;
    private Long maxQueen;
}
