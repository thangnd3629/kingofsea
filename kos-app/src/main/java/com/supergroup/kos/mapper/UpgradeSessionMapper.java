package com.supergroup.kos.mapper;

import java.time.Duration;
import java.time.LocalDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.dto.upgrade.UpgradeStatusResponse;

@Mapper
public interface UpgradeSessionMapper {
    @Mapping(target = "upgradeSessionId", source = "id")
    @Mapping(target = "duration", source = "duration")
    @Mapping(target = "current", source = "timeStart", qualifiedByName = "getCurrentTime")
    UpgradeStatusResponse toUpgradeStatusResponse(UpgradeSession upgradeInfo);

    @Named("getCurrentTime")
    default Long getCurrentTime(LocalDateTime timeStart) {
        return Duration.between(timeStart, LocalDateTime.now()).toMillis();
    }
}
