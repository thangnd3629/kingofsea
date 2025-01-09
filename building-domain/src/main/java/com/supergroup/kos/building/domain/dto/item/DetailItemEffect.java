package com.supergroup.kos.building.domain.dto.item;

import java.time.LocalDateTime;

public interface DetailItemEffect {
    Boolean getIsUsed();

    LocalDateTime getUseTime();

    LocalDateTime getExpiredDate();
}
