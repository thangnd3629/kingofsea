package com.supergroup.kos.building.domain.model.mining;

import java.time.LocalDateTime;

import com.supergroup.kos.building.domain.constant.TypeChangeMiningWood;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MiningWood {
    private Long                 worker; // people mining influence
    private Long                 level;
    private LocalDateTime        timeInfluence;
    private TypeChangeMiningWood type;
}