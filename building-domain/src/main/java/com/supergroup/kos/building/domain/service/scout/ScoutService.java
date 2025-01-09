package com.supergroup.kos.building.domain.service.scout;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.auth.domain.model.UserProfile;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.core.utils.RandomUtil;
import com.supergroup.kos.building.domain.async.ScoutServiceAsyncTask;
import com.supergroup.kos.building.domain.command.ActivityScoutCommand;
import com.supergroup.kos.building.domain.command.GetRelicsCommand;
import com.supergroup.kos.building.domain.command.GetScoutBuildingInfoCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.constant.MissionResult;
import com.supergroup.kos.building.domain.constant.MissionStatus;
import com.supergroup.kos.building.domain.constant.MissionType;
import com.supergroup.kos.building.domain.constant.ScoutMode;
import com.supergroup.kos.building.domain.constant.item.ItemId;
import com.supergroup.kos.building.domain.mapper.ScoutReportMapper;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.config.EscortShipLevelConfig;
import com.supergroup.kos.building.domain.model.config.ScoutCaseConfig;
import com.supergroup.kos.building.domain.model.config.seamap.SpeedSoldierConfig;
import com.supergroup.kos.building.domain.model.mining.ScoutBuilding;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.scout.ConnectionStatus;
import com.supergroup.kos.building.domain.model.scout.Scout;
import com.supergroup.kos.building.domain.model.scout.ScoutReport;
import com.supergroup.kos.building.domain.model.scout.ScoutingResult;
import com.supergroup.kos.building.domain.model.seamap.EscortShipSquad;
import com.supergroup.kos.building.domain.model.seamap.InfoElement;
import com.supergroup.kos.building.domain.model.seamap.ResourceIsland;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.model.ship.EscortShipScoutingResult;
import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.building.domain.model.ship.MotherShipScoutingResult;
import com.supergroup.kos.building.domain.repository.persistence.item.UserItemRepository;
import com.supergroup.kos.building.domain.repository.persistence.scout.ScoutReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.scout.ScoutRepository;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipLevelConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipRepository;
import com.supergroup.kos.building.domain.repository.persistence.ship.MotherShipRepository;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.CastleBuildingService;
import com.supergroup.kos.building.domain.service.building.ScoutBuildingService;
import com.supergroup.kos.building.domain.service.config.KosConfigService;
import com.supergroup.kos.building.domain.service.queen.QueenService;
import com.supergroup.kos.building.domain.service.relic.RelicService;
import com.supergroup.kos.building.domain.service.seamap.CalculateDistanceElementsService;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;
import com.supergroup.kos.building.domain.service.seamap.item.ItemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoutService {

    private final ScoutRepository                  scoutRepository;
    private final ScoutReportRepository            scoutReportRepository;
    private final ScoutBuildingService             scoutBuildingService;
    private final AssetsService                    assetsService;
    private final CastleBuildingService            castleBuildingService;
    private final QueenService                     queenService;
    private final RelicService                     relicService;
    private final UserItemRepository               userItemRepository;
    private final MotherShipRepository             motherShipRepository;
    private final EscortShipRepository             escortShipRepository;
    private final ScoutReportMapper                scoutReportMapper;
    private final EscortShipLevelConfigDataSource  escortShipLevelConfigDataSource;
    private final CalculateDistanceElementsService calculateDistanceElementsService;
    private final KosConfigService                 kosConfigService;
    private final SeaActivityService               seaActivityService;
    private final ScoutServiceAsyncTask            scoutServiceAsyncTask;
    private final SeaElementService                seaElementService;
    private final ItemService                      itemService;
    private final ScoutReportService               scoutReportService;

    public Scout save(Scout scout) {
        return scoutRepository.save(scout);
    }

    @Transactional
    public void activityScout(ActivityScoutCommand command) {
        // valid input
        SeaElement seaElement = seaElementService.findByXAndYFromDatabase(command.getCoordinates().getX(), command.getCoordinates().getY());
        if(Objects.isNull(seaElement) || !seaElement.getActive()) {
            throw KOSException.of(ErrorCode.SEA_ELEMENT_NOT_FOUND);
        }
        if(seaElement instanceof UserBase) {
            KosProfile kosProfileEnemy = ((UserBase) seaElement).getKosProfile();
            ScoutBuilding scoutBuildingEnemy = scoutBuildingService.getBuildingInfo(
                    new GetScoutBuildingInfoCommand(kosProfileEnemy.getId()).setCheckUnlockBuilding(false));
            if (!scoutBuildingEnemy.canScout()) {
                throw KOSException.of(ErrorCode.CANNOT_SCOUT_THIS_USER);
            }
            if(kosProfileEnemy.getId().equals(command.getKosProfile().getId())) {
                throw KOSException.of(ErrorCode.CANNOT_SCOUT_THIS_YOUR_SHELF);
            }
        }

        // check feature scout is unlock
        ScoutBuilding scoutBuildingArmy = scoutBuildingService.getBuildingInfo(new GetScoutBuildingInfoCommand(command.getKosProfile().getId()));
        if (!scoutBuildingArmy.getUnlockScoutFeature()) {
            throw KOSException.of(ErrorCode.FEATURE_SCOUT_IS_LOCKED);
        }

        if (command.getNumberArmy() > scoutBuildingArmy.getAvailableScout()) {
            throw KOSException.of(ErrorCode.NOT_ENOUGH_SOLDIERS);
        }

        // deactivate my anti scout item
        var asset = assetsService.findByKosProfile_Id(command.getKosProfile().getId()).orElseThrow(() -> KOSException.of(ErrorCode.KOS_ASSETS_NOT_FOUND));
        var antiScoutItems = userItemRepository.findUsedItemsByAssetIdAndItemId(asset.getId(), ItemId.WA_9);
        antiScoutItems.forEach(itemService::deactivate);

        // deactivate my blind scout item
        var blindScoutItems = userItemRepository.findUsedItemsByAssetIdAndItemId(asset.getId(), ItemId.WA_13);
        blindScoutItems.forEach(itemService::deactivate);

        Long distance = calculateDistanceElementsService.calculateDistanceBetweenElement(command.getKosProfile().getBase(), seaElement);
        Double speed = getSpeedScout();
        Long missionTime = Math.round(distance / speed);
//        ScoutReport scoutReport = new ScoutReport();
//        scoutReport.setTimeStart(LocalDateTime.now())
//                   .setMissionTime(missionTime * 2)
//                   .setKosProfile(kosProfileArmy)
//                   .setKosProfileTarget(kosProfileEnemy)
//                   .setMissionType(command.getType())
//                   .setMissionStatus(MissionStatus.DOING)
//                   .setNumberArmy(command.getNumberArmy())
//                   .setNumberEnemy(scoutBuildingEnemy.getAvailableScout())
//                   .setSpeed(speed)
//                   .setActive(false)
//                   .setInfoBaseUserTarget(getInfoBaseTargetSeaMap(kosProfileEnemy));

        // create Scout
        Scout scout = new Scout().setTimeStart(LocalDateTime.now())
                                 .setScouter(command.getKosProfile())
                                 .setNumberArmy(command.getNumberArmy())
                                 .setMissionTime(missionTime * 2)
                                 .setMissionType(command.getType())
                                 .setMissionStatus(MissionStatus.DOING)
                                 .setSpeed(speed)
                                 .setTarget(seaElement.getCoordinates())
                                 .setSeaElement(seaElement);
        seaActivityService.startScoutActivity(scout);

        scoutBuildingArmy.setAvailableScout(scoutBuildingArmy.getAvailableScout() - command.getNumberArmy());
        scoutBuildingService.save(scoutBuildingArmy);

    }

    public ScoutReport createScoutReport(Scout scout,
                                         MissionResult missionResult,
                                         KosProfile scouter,
                                         KosProfile target,
                                         Long numberArmy,
                                         Long numberEnemy) {
        ScoutReport scoutReport = new ScoutReport();
        scoutReport.setTimeStart(scout.getTimeStart())
                   .setMissionTime(scout.getMissionTime())
                   .setKosProfile(scouter)
                   .setKosProfileTarget(target)
                   .setMissionType(scout.getMissionType())
                   .setNumberArmy(numberArmy)
                   .setNumberEnemy(numberEnemy)
                   .setSpeed(scout.getSpeed())
                   .setResult(missionResult)
                   .setInfoElementTarget(getInfoElement(scout.getSeaElement()))
                   .setScout(scout)
                   .setNavigate(scout.getTarget());
        return scoutReport;
    }

    public void updateScout(Scout scout) {
        SeaElement seaElementTarget = seaElementService.findByXAndYFromDatabase(scout.getTarget().getX(), scout.getTarget().getY());
        if(Objects.isNull(seaElementTarget) || !seaElementTarget.getActive()){
            scout.setResult(MissionResult.NOT_FOUND_ENEMY_BASE);
            scoutServiceAsyncTask.sendNoTargetFoundNotification(scout.getScouter());
        } else {
            if (seaElementTarget instanceof UserBase) {
                UserBase userBase = (UserBase) seaElementTarget;
                ScoutBuilding scoutBuildingEnemy = scoutBuildingService.getBuildingInfo(
                        new GetScoutBuildingInfoCommand(userBase.getKosProfile().getId()).setCheckUnlockBuilding(false));

                 // valid
                if (!scoutBuildingEnemy.canScout()) {
                    scout.setResult(MissionResult.CANCEL);
                    // sent notification if you need
                    return;
                }
                if(scout.getScouter().getId().equals(userBase.getKosProfile().getId())) {
                    scout.setResult(MissionResult.CANCEL);
                    // sent notification if you need
                    return;
                }
                scout.setNumberEnemy(scoutBuildingEnemy.getAvailableScout()); // get numberEnemy
                scout.setKosProfileTarget(userBase.getKosProfile());
                scout.setSeaElement(userBase);
                if (userBase.isOccupied()) {
                    scout.setScoutMode(ScoutMode.SCOUT_OCCUPY_BASE);
                } else {
                    scout.setScoutMode(ScoutMode.SCOUT_NORMAL_BASE);
                }
            } else if (seaElementTarget instanceof ResourceIsland) {
                ResourceIsland resourceIsland = (ResourceIsland) seaElementTarget;
                if (Objects.nonNull(resourceIsland.getMiningSession())) {
                    scout.setNumberEnemy(0L).setScoutMode(ScoutMode.SCOUT_RESOURCE_ISLAND);
                    scout.setKosProfileTarget(resourceIsland.getMiningSession().getSeaActivity().getKosProfile());
                    scout.setSeaElement(resourceIsland);
                } else {
                    scout.setResult(MissionResult.NOT_FOUND_ENEMY_BASE);
                    scoutServiceAsyncTask.sendNoTargetFoundNotification(scout.getScouter());
                }
            } else {
                scout.setResult(MissionResult.NOT_FOUND_ENEMY_BASE);
                scoutServiceAsyncTask.sendNoTargetFoundNotification(scout.getScouter());
            }
        }
    }

//    @Transactional
//    public ScoutReport scoutInEnemyPlace(Long scoutId) {
//        Scout scout = scoutRepository.findById(scoutId).orElseThrow(()-> KOSException.of(ErrorCode.SCOUT_NOT_FOUND));
//        updateScout(scout);
//        ScoutCaseConfig scoutCaseConfig = scoutBuildingService.getScoutCaseConfigByNumberEnemy(
//                                                                      new GetScoutCaseConfigCommand().setEnemy(scout.getNumberEnemy())).stream()
//                                                              .filter(sc -> sc.getNumberArmy().equals(scout.getNumberArmy()))
//                                                              .findFirst().orElseThrow(() -> KOSException.of(ErrorCode.SCOUT_CONFIG_NOT_FOUND));
//        MissionResult missionResult = getMissionResult(scoutCaseConfig);
//        List<ScoutReport> scoutReports = createScoutReport(scout, missionResult);
//        switch (missionResult) {
//            case SUCCESS:
//                scoutReport.setInfoReceiveModel(
//                        getInfoReceive(scoutReport.getKosProfileTarget().getId(), scoutReport.getMissionType()));
//
//                // check Enemy use items BlindScout
//                checkUseBlindScout(scoutReport).setUpdatedAt(LocalDateTime.now());
//                // save to db
//                scoutReportRepository.save(scoutReport);
//                break;
//            case FAIL:
//            case BETRAYED:
//                Long soliderDie = Math.round(scoutReport.getNumberArmy() * scoutCaseConfig.getRateDie());
//                ScoutingResult scoutingResult = getInfoReceive(scoutReport.getKosProfile().getId(), scoutReport.getMissionType());
//
//                // counter scout
//                if (missionResult == MissionResult.BETRAYED) {
//                    ScoutReport reportCounterScout = new ScoutReport();
//                    reportCounterScout.setKosProfile(scoutReport.getKosProfileTarget())
//                                      .setKosProfileTarget(scoutReport.getKosProfile())
//                                      .setResult(MissionResult.COUNTER_SCOUT)
//                                      .setNumberArmy(scoutReport.getNumberEnemy())
//                                      .setNumberEnemy(scoutReport.getNumberArmy())
//                                      .setInfoReceiveModel(scoutingResult)
//                                      .setTimeStart(scoutReport.getTimeStart())
//                                      .setMissionStatus(MissionStatus.DONE)
//                                      .setMissionTime(scoutReport.getMissionTime())
//                                      .setMissionType(scoutReport.getMissionType())
//                                      .setActive(true)
//                                      .setInfoBaseUserTarget(getInfoBaseTargetSeaMap(scoutReport.getKosProfile())); // when betrayed swap(army, enemy)
//                    // sent notification to enemy
//                    scoutServiceAsyncTask.sendBetrayedScoutNotification(reportCounterScout);
//                    scoutReportRepository.save(reportCounterScout);
//                }
//
//                scoutingResult.setSoliderDie(soliderDie);
//                scoutReport.setInfoReceiveModel(scoutingResult)
//                           .setUpdatedAt(LocalDateTime.now());
//
//                // save to db
//                scoutReportRepository.save(scoutReport);
//                break;
//        }
//        return scoutReport;
//    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void finalScout(Long scoutId) {
        Optional<Scout> optional = scoutRepository.findById(scoutId);
        if (optional.isPresent() && !optional.get().getMissionStatus().equals(MissionStatus.DONE)) {
            Scout scout = optional.get();
            List<ScoutReport> scoutReports = scoutReportRepository.findByScout_Id(scoutId);
            ScoutBuilding scoutBuildingArmy = scoutBuildingService.getBuildingInfo(
                    new GetScoutBuildingInfoCommand(scout.getScouter().getId()));
            scoutBuildingArmy.setTotalScout(scoutBuildingArmy.getTotalScout() - scout.getSoliderDie())
                             .setAvailableScout(scoutBuildingArmy.getAvailableScout() + scout.getSoliderRemain());


            scoutReportService.activeScoutReport(scoutReports);
            scoutBuildingService.save(scoutBuildingArmy);
            save(scout.setMissionStatus(MissionStatus.DONE));
            // sent notification to army
            scoutServiceAsyncTask.sendScoutNotification(
                    scoutReports.stream().filter(s -> !s.getResult().equals(MissionResult.COUNTER_SCOUT)).collect(Collectors.toList()));
        }
    }

    public MissionResult getMissionResult(ScoutCaseConfig scoutCaseConfig) {
        if (RandomUtil.random(scoutCaseConfig.getRateSuccess().floatValue())) {
            return MissionResult.SUCCESS;
        } else {
            if (scoutCaseConfig.getIsBetrayed()) {
                return MissionResult.BETRAYED;
            } else {
                return MissionResult.FAIL;
            }
        }
    }

    public Double getSpeedScout() {
        SpeedSoldierConfig speedSoldierConfig = kosConfigService.getSpeedSoldierConfig();
        return speedSoldierConfig.getSpeed();
    }

    public void updateUserProfileScoutingResult(ScoutingResult scoutingResult, KosProfile kosProfile) {
        UserProfile userProfile = kosProfile.getUser().getUserProfile();
        scoutingResult.setUsername(userProfile.getUsername())
                      .setAvatarUrl(userProfile.getAvatarUrl());
        UserBase userBase = kosProfile.getBase();
        if (Objects.nonNull(userBase)) {
            scoutingResult.setX(userBase.getX())
                          .setY(userBase.getY());
        }
    }


    public ScoutingResult getAssetByKosProfileId(Long kosProfileEnemyId) {
        ScoutingResult scoutingResult = new ScoutingResult();
        castleBuildingService.claimPeopleAndGold(kosProfileEnemyId);
        List<Long> queenList = queenService.getQueens(kosProfileEnemyId).stream()
                                           .map(q -> q.getQueenConfig().getId()).collect(Collectors.toList());
        List<Long> relicList = relicService.getRelics(new GetRelicsCommand().setKosProfileId(kosProfileEnemyId).setIsListing(true))
                                           .stream().map(r -> r.getRelicConfig().getId()).collect(Collectors.toList());
        Assets assets = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfileEnemyId));
        scoutingResult.setWood(assets.getWood().longValue())
                      .setStone(assets.getStone().longValue())
                      .setGold(assets.getGold().longValue())
                      .setQueens(queenList)
                      .setRelics(relicList)
        ;
        return scoutingResult;
    }

    public ScoutingResult getMilitaryByKosProfileId(Long kosProfileEnemyId) {
        ScoutingResult scoutingResult = new ScoutingResult();
        List<EscortShipScoutingResult> escortShips = escortShipRepository.findByKosProfileId(kosProfileEnemyId).stream().map(es -> {
            EscortShipScoutingResult escortShipScoutingResult = scoutReportMapper.toEscortShipScoutingResult(es);
            EscortShipLevelConfig escortShipLevelConfig = escortShipLevelConfigDataSource.getByTypeAndLevel(
                    escortShipScoutingResult.getType(),
                    escortShipScoutingResult.getLevel());

            escortShipScoutingResult.setPercentStatLevel(escortShipLevelConfig.getPercentStat());
            return escortShipScoutingResult;
        }).collect(Collectors.toList());
        List<MotherShipScoutingResult> motherShips = scoutReportMapper.toMotherShipScoutingResults(
                motherShipRepository.findByKosProfileId(kosProfileEnemyId));

        // set result
        scoutingResult.setEscortShips(escortShips).setMotherShips(motherShips);
        return scoutingResult;
    }

    public ConnectionStatus getConnectionStatusUser(Long kosProfileEnemyId) {
        ConnectionStatus connectionStatus = new ConnectionStatus();
        // todo ipm late
        if (RandomUtil.random(0.5F)) {
            connectionStatus.setIsOnline(true).setLastActiveFrom(null);
        } else {
            connectionStatus.setIsOnline(false).setLastActiveFrom((long) new Random().nextInt(10000));
        }
        return connectionStatus;
    }

    public ScoutReport checkUseBlindScout(ScoutReport scoutReport) {
        if (scoutReport.getMissionType().equals(MissionType.MILITARY)) {
            ScoutBuilding scoutBuildingEnemy = scoutBuildingService.getBuildingInfo(
                    new GetScoutBuildingInfoCommand(scoutReport.getKosProfileTarget().getId()).setCheckUnlockBuilding(false));
            if (scoutBuildingEnemy.isBlindScout()) {
                ScoutingResult scoutingResult = scoutReport.getInfoReceiveModel();
                scoutingResult.setEscortShips(scoutingResult.getEscortShips().stream().map(is -> {
                    if (Objects.nonNull(is.getAmount())) {
                        is.setAmount(Double.valueOf(is.getAmount() * scoutBuildingEnemy.getBlindMulti()).longValue());
                    }
                    return is;
                }).collect(Collectors.toList()));
                scoutingResult.setMotherShips(doubleResultMotherShip(scoutingResult.getMotherShips()));
                scoutReport.setInfoReceiveModel(scoutingResult);
            }
        }
        return scoutReport;
    }

    public List<MotherShipScoutingResult> doubleResultMotherShip(List<MotherShipScoutingResult> list) {
        List<MotherShipScoutingResult> result = new ArrayList<>();
        for (MotherShipScoutingResult motherShipScoutingResult : list) {
            result.add(motherShipScoutingResult);
            result.add(motherShipScoutingResult);
        }
        return result;
    }

    public InfoElement getInfoElement(SeaElement seaElement) {
        return Objects.nonNull(seaElement) ?
               new InfoElement().setIslandName(seaElement.name()).setCoordinates(seaElement.getCoordinates()) : null;
    }

    public ScoutingResult getMilitaryFromShipLineUp(List<ShipLineUp> shipLineUps, KosProfile kosProfile) {
        ScoutingResult scoutingResult = new ScoutingResult();
        List<MotherShip> motherShips = new ArrayList<>();
        List<EscortShipSquad> escortShipSquads = new ArrayList<>();
        for(ShipLineUp shipLineUp : shipLineUps) {
            List<EscortShipSquad> squads = shipLineUp.getEscortShipSquad();
            escortShipSquads.addAll(squads);
            if(Objects.nonNull(shipLineUp.getMotherShip())) {
                motherShips.add(shipLineUp.getMotherShip());
            }
        }

        List<EscortShipScoutingResult> escortShipScoutingResults = escortShipRepository.findByKosProfileId(kosProfile.getId()).stream().map(es -> {
            EscortShipScoutingResult escortShipScoutingResult = scoutReportMapper.toEscortShipScoutingResult(es);
            EscortShipLevelConfig escortShipLevelConfig = escortShipLevelConfigDataSource.getByTypeAndLevel(
                    escortShipScoutingResult.getType(),
                    escortShipScoutingResult.getLevel());

            escortShipScoutingResult.setPercentStatLevel(escortShipLevelConfig.getPercentStat());
            return escortShipScoutingResult;
        }).collect(Collectors.toList());
        List<MotherShipScoutingResult> motherShipScoutingResults = scoutReportMapper.toMotherShipScoutingResults(motherShips);

        // update amount for EscortShipScoutingResult
        var mapStaticEscortShip = statisticEscortShip(escortShipSquads);
        for(EscortShipScoutingResult escortShipScoutingResult: escortShipScoutingResults) {
            escortShipScoutingResult.setAmount(mapStaticEscortShip.getOrDefault(escortShipScoutingResult.getId(), 0L));
        }
        // set result
        scoutingResult.setEscortShips(escortShipScoutingResults).setMotherShips(motherShipScoutingResults);
        return scoutingResult;
    }

    private Map<Long, Long> statisticEscortShip(List<EscortShipSquad> escortShipSquads) {
        Map<Long, Long> map = new HashMap<>();
        for(EscortShipSquad escortShipSquad: escortShipSquads) {
            Long escortShipId = escortShipSquad.getEscortShip().getId();
            if(map.containsKey(escortShipId)) {
                Long amount = map.get(escortShipId);
                map.put(escortShipId, amount + escortShipSquad.getRemain());
            } else {
                map.put(escortShipId, escortShipSquad.getRemain());
            }
        }
        return map;
    }
}
