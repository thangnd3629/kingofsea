package com.supergroup.kos.building.domain.service.scout;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.MissionResult;
import com.supergroup.kos.building.domain.model.scout.Scout;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Service
@Accessors(chain = true)
@RequiredArgsConstructor
@Slf4j
public class ScoutModeFactory {
    private final ScoutNormalBase     scoutNormalBase;
    private final ScoutOccupyBase     scoutOccupyBase;
    private final ScoutResourceIsland scoutResourceIsland;
    private final ScoutService        scoutService;

    public ScoutMode getMode(Scout scout) {
        scoutService.updateScout(scout);
        scoutService.save(scout);
        if(Objects.nonNull(scout.getScoutMode())) {
            switch (scout.getScoutMode()) {
                case SCOUT_NORMAL_BASE:
                    return scoutNormalBase;
                case SCOUT_OCCUPY_BASE:
                    return scoutOccupyBase;
                case SCOUT_RESOURCE_ISLAND:
                    return scoutResourceIsland;
                default:
                    throw KOSException.of(ErrorCode.BAD_REQUEST_ERROR);
            }
        }
        log.info("Scout id {} , get mode null ", scout.getId());
        return null;
    }
}
