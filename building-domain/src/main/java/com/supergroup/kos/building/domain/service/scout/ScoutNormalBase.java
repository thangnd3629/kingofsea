package com.supergroup.kos.building.domain.service.scout;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.ScoutServiceAsyncTask;
import com.supergroup.kos.building.domain.command.GetScoutCaseConfigCommand;
import com.supergroup.kos.building.domain.constant.MissionResult;
import com.supergroup.kos.building.domain.constant.MissionType;
import com.supergroup.kos.building.domain.model.config.ScoutCaseConfig;
import com.supergroup.kos.building.domain.model.scout.ConnectionStatus;
import com.supergroup.kos.building.domain.model.scout.Scout;
import com.supergroup.kos.building.domain.model.scout.ScoutReport;
import com.supergroup.kos.building.domain.model.scout.ScoutTargetInfo;
import com.supergroup.kos.building.domain.model.scout.ScoutingResult;
import com.supergroup.kos.building.domain.repository.persistence.scout.ScoutReportRepository;
import com.supergroup.kos.building.domain.service.building.ScoutBuildingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScoutNormalBase  implements ScoutMode {
    private final ScoutReportRepository           scoutReportRepository;
    private final ScoutBuildingService            scoutBuildingService;
    private final ScoutServiceAsyncTask           scoutServiceAsyncTask;
    private final ScoutService                    scoutService;

    @Override
    @Transactional
    public void scoutInEnemyPlace(Scout scout) {
        if (getMissionType().contains(scout.getMissionType())) {
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
                            getInfoReceive(new ScoutTargetInfo().setKosProfileTarget(scout.getKosProfileTarget()),
                                           scoutReport.getMissionType()));

                    // check Enemy use items BlindScout
                    scoutService.checkUseBlindScout(scoutReport).setUpdatedAt(LocalDateTime.now());
                    // save to db
                    scoutReportRepository.save(scoutReport);
                    break;
                case FAIL:
                case BETRAYED:
                    Long soliderDie = Math.round(scoutReport.getNumberArmy() * scoutCaseConfig.getRateDie());
                    scout.setSoliderDie(soliderDie);
                    ScoutingResult scoutingResult = getInfoReceive(new ScoutTargetInfo().setKosProfileTarget(scout.getScouter()),
                                                                   scoutReport.getMissionType());

                    // counter scout
                    if (missionResult == MissionResult.BETRAYED) {
                        ScoutReport reportCounterScout = scoutService.createScoutReport(scout, MissionResult.COUNTER_SCOUT,
                                                                                        scout.getKosProfileTarget(),
                                                                                        scout.getScouter(),
                                                                                        scout.getNumberEnemy(),
                                                                                        scout.getNumberArmy());
                        reportCounterScout.setInfoReceiveModel(scoutingResult).setActive(true);
                        // sent notification to enemy
                        scoutServiceAsyncTask.sendBetrayedScoutNotification(List.of(reportCounterScout));
                        scoutReportRepository.save(reportCounterScout);
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

    @Override
    public ScoutingResult getAssets(ScoutTargetInfo targetInfo) {
        Long kosProfileEnemyId = targetInfo.getKosProfileTarget().getId();
        ScoutingResult scoutingResult = scoutService.getAssetByKosProfileId(kosProfileEnemyId);
        scoutService.updateUserProfileScoutingResult(scoutingResult, targetInfo.getKosProfileTarget());
        return scoutingResult;
    }

    @Override
    public ScoutingResult getMilitary(ScoutTargetInfo targetInfo) {
        Long kosProfileEnemyId = targetInfo.getKosProfileTarget().getId();
        ScoutingResult scoutingResult = scoutService.getMilitaryByKosProfileId(kosProfileEnemyId);
        scoutService.updateUserProfileScoutingResult(scoutingResult, targetInfo.getKosProfileTarget());
        return scoutingResult;
    }

    @Override
    public ScoutingResult getConnectionStatus(ScoutTargetInfo targetInfo) {
        ScoutingResult scoutingResult = new ScoutingResult();
        ConnectionStatus connectionStatus = scoutService.getConnectionStatusUser(targetInfo.getKosProfileTarget().getId());
        scoutingResult.setInOnline(connectionStatus.getIsOnline())
                      .setLastActiveFrom(connectionStatus.getLastActiveFrom());
        scoutService.updateUserProfileScoutingResult(scoutingResult, targetInfo.getKosProfileTarget());
        return scoutingResult;
    }

    @Override
    public List<MissionType> getMissionType() {
        return List.of(MissionType.CONNECTION_STATUS, MissionType.ASSETS, MissionType.MILITARY);
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
}
