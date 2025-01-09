package com.supergroup.kos.dto.scout;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ScoutCaseConfigResponse {
    private Long   numberArmy;
    private Double rateSuccess;
    private Double rateDie;
}
