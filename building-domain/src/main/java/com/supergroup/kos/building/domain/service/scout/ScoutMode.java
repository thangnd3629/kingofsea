package com.supergroup.kos.building.domain.service.scout;

import java.util.List;

import com.supergroup.kos.building.domain.constant.MissionType;
import com.supergroup.kos.building.domain.model.scout.Scout;
import com.supergroup.kos.building.domain.model.scout.ScoutTargetInfo;
import com.supergroup.kos.building.domain.model.scout.ScoutingResult;

public interface ScoutMode {
    void scoutInEnemyPlace(Scout scout);

    ScoutingResult getAssets(ScoutTargetInfo targetInfo);

    ScoutingResult getMilitary(ScoutTargetInfo targetInfo);

    ScoutingResult getConnectionStatus(ScoutTargetInfo targetInfo);

    List<MissionType> getMissionType();
}
