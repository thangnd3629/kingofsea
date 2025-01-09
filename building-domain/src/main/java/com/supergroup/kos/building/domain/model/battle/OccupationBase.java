package com.supergroup.kos.building.domain.model.battle;

import java.time.LocalDateTime;

public interface OccupationBase {
    Long getElementId();

    Long getElementConfigId();

    String getName();

    Long getLevel();

    String getAvatarUrl();

    Long getX();

    Long getY();

    LocalDateTime getTimeStart();
}
