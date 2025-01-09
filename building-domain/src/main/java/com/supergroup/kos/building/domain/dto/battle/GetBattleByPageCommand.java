package com.supergroup.kos.building.domain.dto.battle;

import org.springframework.data.domain.Pageable;

import com.supergroup.kos.building.domain.model.profile.KosProfile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GetBattleByPageCommand {
    private final KosProfile kosProfile;
    private final Pageable   pageable;
}
