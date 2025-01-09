package com.supergroup.kos.dto.scout;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ScoutConfigDetail {
    private Long level;
    private Long capacity;
    private Long gpPointReward;
}
