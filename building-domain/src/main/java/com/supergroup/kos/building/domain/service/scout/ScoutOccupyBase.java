package com.supergroup.kos.building.domain.service.scout;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.ScoutServiceAsyncTask;
import com.supergroup.kos.building.domain.command.GetScoutCaseConfigCommand;
import com.supergroup.kos.building.domain.constant.MissionResult;
import com.supergroup.kos.building.domain.constant.MissionType;
import com.supergroup.kos.building.domain.model.config.ScoutCaseConfig;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.scout.ConnectionStatus;
import com.supergroup.kos.building.domain.model.scout.Scout;
import com.supergroup.kos.building.domain.model.scout.ScoutReport;
import com.supergroup.kos.building.domain.model.scout.ScoutTargetInfo;
import com.supergroup.kos.building.domain.model.scout.ScoutingResult;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.repository.persistence.scout.ScoutReportRepository;
import com.supergroup.kos.building.domain.service.building.ScoutBuildingService;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScoutOccupyBase  implements ScoutMode {
    private final ScoutReportRepository           scoutReportRepository;
    private final ScoutBuildingService            scoutBuildingService;
    private final ScoutServiceAsyncTask           scoutServiceAsyncTask;
    private final ScoutService                    scoutService;
    private final SeaActivityService              seaActivityService;

    @Override
    @Transactional
    public void scoutInEnemyPlace(Scout scout) {
        if(getMissionType().contains(scout.getMissionType())) {
            ScoutCaseConfig scoutCaseConfig = scoutBuildingService.getScoutCaseConfigByNumberEnemy(
                                                                          new GetScoutCaseConfigCommand().setEnemy(scout.getNumberEnemy())).stream()
                                                                  .filter(sc -> sc.getNumberArmy().equals(scout.getNumberArmy()))
                                                                  .findFirst().orElseThrow(() -> KOSException.of(ErrorCode.SCOUT_CONFIG_NOT_FOUND));
            MissionResult missionResult = scoutService.getMissionResult(scoutCaseConfig);
            scout.setResult(missionResult);
            ScoutReport scoutReport = scoutService.createScoutReport(scout, missionResult, scout.getScouter(), scout.getKosProfileTarget(),
                                                                     scout.getNumberArmy(), scout.getNumberEnemy());
            switch (missionResult) {
                case SUCCESS:
                    scoutReport.setInfoReceiveModel(
                            getInfoReceive(new ScoutTargetInfo().setSeaElement(scout.getSeaElement()), scoutReport.getMissionType()));

                    // save to db
                    scoutReportRepository.save(scoutReport);
                    break;
                case FAIL:
                case BETRAYED:
                    Long soliderDie = Math.round(scout.getNumberArmy() * scoutCaseConfig.getRateDie());
                    scout.setSoliderDie(soliderDie);
                    ScoutingResult scoutingResult = getInfoReceive(new ScoutTargetInfo().setKosProfileTarget(scout.getScouter()),
                                                                   scoutReport.getMissionType());
                    // counter scout
                    if (missionResult == MissionResult.BETRAYED) {
                        List<KosProfile> kosProfileOccupyBases = getListKosProfileOccupyBase(scout.getSeaElement());
                        List<ScoutReport> reportCounterScouts = new ArrayList<>();
                        for(KosProfile kosProfile: kosProfileOccupyBases) {
                            ScoutReport scoutReport1 = scoutService.createScoutReport(scout, MissionResult.COUNTER_SCOUT, kosProfile, scout.getScouter(),
                                                                                      scout.getNumberEnemy(), scout.getNumberArmy());
                            scoutReport1.setInfoReceiveModel(scoutingResult);
                            reportCounterScouts.add(scoutReport1);
                        }
                        // sent notification to enemy
                        scoutServiceAsyncTask.sendBetrayedScoutNotification(reportCounterScouts);
                        scoutReportRepository.saveAll(reportCounterScouts);
                    }

                    scoutingResult.setSoliderDie(soliderDie);
                    scoutReport.setInfoReceiveModel(scoutingResult)
                               .setUpdatedAt(LocalDateTime.now());

                    // save to db
                    scoutReportRepository.save(scoutReport);
                    break;
            }
        } else {
            scout.setResult(MissionResult.MISSION_TYPE_NOT_VALID);
        }
        scout.setUpdatedAt(LocalDateTime.now());
        scoutService.save(scout);
    }

    private List<KosProfile> getListKosProfileOccupyBase(SeaElement seaElement) {
        List<SeaActivity> seaActivities = seaActivityService.getListOccupyInElement(seaElement.getId());
        Map<Long , KosProfile> map = new HashMap<>();
        for(SeaActivity seaActivity : seaActivities) {
            if(Objects.nonNull(seaActivity.getKosProfile())) {
                map.put(seaActivity.getKosProfile().getId(), seaActivity.getKosProfile());
            }
        }
        return new ArrayList<>(map.values());
//        return (List<KosProfile>) map.values();
    }

    @Override
    public ScoutingResult getAssets(ScoutTargetInfo targetInfo) {
//        Long kosProfileEnemyId = targetInfo.getKosProfileTarget().getId();
        ScoutingResult scoutingResult = new ScoutingResult();
        // do not getAssets
        return scoutingResult;
    }

    @Override
    public ScoutingResult getMilitary(ScoutTargetInfo targetInfo) {
        ScoutingResult scoutingResult = null;
        if(Objects.nonNull(targetInfo.getSeaElement())) {
            List<ShipLineUp> shipLineUps = seaActivityService.getListOccupyInElement(targetInfo.getSeaElement().getId()).stream()
                                                             .map(SeaActivity::getLineUp)
                                                             .filter(Objects::nonNull).collect(Collectors.toList());
            if(targetInfo.getSeaElement().isOccupied()) {
                KosProfile kosProfileTarget = targetInfo.getSeaElement().getInvader().getKosProfileInvader();
                scoutingResult = scoutService.getMilitaryFromShipLineUp(shipLineUps, kosProfileTarget);
                scoutService.updateUserProfileScoutingResult(scoutingResult, kosProfileTarget);
            } else {
                log.info("Scout : scout occupy base not is occupy, element id : {}", targetInfo.getSeaElement().getId());
            }
        } else {
            scoutingResult = scoutService.getMilitaryByKosProfileId(targetInfo.getKosProfileTarget().getId());
            scoutService.updateUserProfileScoutingResult(scoutingResult, targetInfo.getKosProfileTarget());
        }

        return scoutingResult;
    }

    @Override
    public ScoutingResult getConnectionStatus(ScoutTargetInfo targetInfo) {
        KosProfile kosProfileTarget = null;
        if(Objects.nonNull(targetInfo.getSeaElement())) {
            // todo
            List<KosProfile> kosProfilesOccupy = getListKosProfileOccupyBase(targetInfo.getSeaElement());
            if(kosProfilesOccupy.size() < 1 ) {
                log.info("Scout : scout base occupy , not find kosProfile occupy, element id ; {}", targetInfo.getSeaElement().getId());
            }
            kosProfileTarget = kosProfilesOccupy.get(0);
        } else {
            kosProfileTarget = targetInfo.getKosProfileTarget();
        }

        ScoutingResult scoutingResult = new ScoutingResult();
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
