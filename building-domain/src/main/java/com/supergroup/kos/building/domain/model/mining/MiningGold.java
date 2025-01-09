package com.supergroup.kos.building.domain.model.mining;

import java.time.LocalDateTime;

import com.supergroup.kos.building.domain.constant.TypeChangeMiningGold;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MiningGold {
    private Long                 idler;
    private Long                 level;
    private TypeChangeMiningGold type;
    private LocalDateTime        timeInfluence;
}
