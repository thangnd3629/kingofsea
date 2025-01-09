package com.supergroup.kos.building.domain.dto.seamap;

import java.time.LocalDateTime;

import com.supergroup.kos.building.domain.constant.seamap.BossSeaStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BossSeaCache extends SeaElementCache {

    private Long          hpLost;
    private BossSeaStatus status;
    private LocalDateTime timeRevivingEnd;
}
