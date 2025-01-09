package com.supergroup.kos.building.domain.service.scout;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.MissionResult;
import com.supergroup.kos.building.domain.constant.MissionType;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.scout.ConnectionStatus;
import com.supergroup.kos.building.domain.model.scout.Scout;
import com.supergroup.kos.building.domain.model.scout.ScoutReport;
import com.supergroup.kos.building.domain.model.scout.ScoutTargetInfo;
import com.supergroup.kos.building.domain.model.scout.ScoutingResult;
import com.supergroup.kos.building.domain.model.seamap.ResourceIsland;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.repository.persistence.scout.ScoutReportRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScoutResourceIsland implements ScoutMode {
    private final ScoutReportRepository           scoutReportRepository;
    private final ScoutService                    scoutService;


    @Override
    @Transactional
    public void scoutInEnemyPlace(Scout scout) {
        if(getMissionType().contains(scout.getMissionType())) {
            MissionResult missionResult = MissionResult.SUCCESS; // 100% success
            scout.setResult(missionResult);
            scout.setSoliderDie(0L);
            ScoutReport scoutReport = scoutService.createScoutReport(scout, missionResult, scout.getScouter(), scout.getKosProfileTarget(), scout.getNumberArmy(), scout.getNumberEnemy());
            scoutReport.setInfoReceiveModel(
                    getInfoReceive(new ScoutTargetInfo().setSeaElement(scout.getSeaElement()), scoutReport.getMissionType()));

            // save to db
            scoutReportRepository.save(scoutReport);
        } else {
            scout.setResult(MissionResult.MISSION_TYPE_NOT_VALID);
        }
        scout.setUpdatedAt(LocalDateTime.now());
        scoutService.save(scout);
    }


    @Override
    public ScoutingResult getAssets(ScoutTargetInfo targetInfo) {
        return new ScoutingResult();
    }

    @Override
    public ScoutingResult getMilitary(ScoutTargetInfo targetInfo) {
        ScoutingResult scoutingResult = new ScoutingResult();
        ResourceIsland resourceIsland = (ResourceIsland) targetInfo.getSeaElement();
        if(Objects.isNull(resourceIsland.getMiningSession())) {
            return scoutingResult;
        }
        List<ShipLineUp> shipLineUps = List.of(resourceIsland.getMiningSession().getSeaActivity().getLineUp());
        scoutingResult = scoutService.getMilitaryFromShipLineUp(shipLineUps, resourceIsland.getMiningSession().getSeaActivity().getKosProfile());
        scoutService.updateUserProfileScoutingResult(scoutingResult, resourceIsland.getMiningSession().getSeaActivity().getKosProfile());
        return scoutingResult;
    }

    @Override
    public ScoutingResult getConnectionStatus(ScoutTargetInfo targetInfo) {
        ScoutingResult scoutingResult = new ScoutingResult();
        ResourceIsland resourceIsland = (ResourceIsland) targetInfo.getSeaElement();
        if(Objects.isNull(resourceIsland.getMiningSession())) {
            return scoutingResult;
        }
        KosProfile kosProfileTarget = resourceIsland.getMiningSession().getSeaActivity().getKosProfile();
        ConnectionStatus connectionStatus = scoutService.getConnectionStatusUser(kosProfileTarget.getId());
        scoutingResult.setInOnline(connectionStatus.getIsOnline())
                .setLastActiveFrom(connectionStatus.getLastActiveFrom());
        scoutService.updateUserProfileScoutingResult(scoutingResult, kosProfileTarget);
        return scoutingResult;
    }

    public ScoutingResult getInfoReceive(ScoutTargetInfo targetInfo, MissionType missionType) {
        switch (missionType) {
            case CONNECTION_STATUS:
                return getConnectionStatus(targetInfo);
            case ASSETS:
                return getAssets(targetInfo);
            case MILITARY:
                return getMilitary(targetInfo);
            default:
                throw KOSException.of(ErrorCode.MISSION_TYPE_NOT_FOUND);
        }
    }

    @Override
    public List<MissionType> getMissionType() {
        return List.of(MissionType.CONNECTION_STATUS, MissionType.MILITARY);
    }
}
