package com.supergroup.kos.dto.building;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ScoutTrainingStatus {
    private Long duration;
    private Long current;
    private Long scoutStraining;


}
