package com.supergroup.kos.building.domain.dto.battle;

import com.supergroup.kos.building.domain.model.seamap.BossSea;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RevivalBossEvent {
    private final BossSea bossSea;
}
