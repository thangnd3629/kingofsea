package com.supergroup.kos.building.domain.dto.seamap;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ResourceIslandCache extends SeaElementCache {
    private Double                        mined;
    private MiningResourceSeaSessionCache miningSession;
}
