package com.supergroup.kos.building.domain.service.seamap.activity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.MotherShipServiceAsyncTask;
import com.supergroup.kos.building.domain.async.SeaActivityAsyncTask;
import com.supergroup.kos.building.domain.command.GetCommandBuildingInfo;
import com.supergroup.kos.building.domain.command.GetMilitaryBuildingInfo;
import com.supergroup.kos.building.domain.command.InitSeaActivityCommand;
import com.supergroup.kos.building.domain.command.WithdrawActivityCommand;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.constant.seamap.SeaActivityStatus;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.building.LighthouseBuilding;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.scout.Scout;
import com.supergroup.kos.building.domain.model.seamap.BossSea;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.Invader;
import com.supergroup.kos.building.domain.model.seamap.ResourceIsland;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.model.seamap.movesession.MissionType;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;
import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.building.domain.repository.persistence.scout.ScoutRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaActivityRepository;
import com.supergroup.kos.building.domain.service.battle.OccupyService;
import com.supergroup.kos.building.domain.service.building.CommandBuildingService;
import com.supergroup.kos.building.domain.service.building.LighthouseBuildingService;
import com.supergroup.kos.building.domain.service.building.MilitaryBuildingService;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;
import com.supergroup.kos.building.domain.service.seamap.activity.withdraw.WithdrawHandlerFactory;
import com.supergroup.kos.building.domain.utils.DistanceUtils;
import com.supergroup.kos.building.domain.utils.SeaMapCoordinatesUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeaActivityService {

    private final MoveSessionService         moveSessionService;
    private final UserBaseService            userBaseService;
    private final SeaElementService          seaElementService;
    private final SeaActivityRepository      repository;
    private final LineUpService              lineUpService;
    private final ScoutRepository            scoutRepository;
    private final LighthouseBuildingService  lightHouseBuildingService;
    private final WithdrawHandlerFactory     withdrawHandlerFactory;
    private final SeaActivityAsyncTask       seaActivityAsyncTask;
    private final OccupyService              occupyService;
    private final MilitaryBuildingService    militaryBuildingService;
    private final CommandBuildingService     commandBuildingService;
    private final MotherShipServiceAsyncTask motherShipServiceAsyncTask;

    @Transactional
    public SeaActivity initActivity(KosProfile kosProfile, InitSeaActivityCommand command) {
        SeaElement destination = seaElementService.getElementById(command.getDestinationId());
        UserBase userBase = userBaseService.getByKosProfileId(kosProfile.getId());
        ShipLineUp lineUp = lineUpService.getLineUpById(kosProfile.getId(), command.getLineUpId());
        MissionType missionType = command.getMissionType();
        eligibleToStartActivity(kosProfile, destination.getId(), missionType, lineUp);
        SeaActivity activity;
        switch (destination.type()) {
            case USER_BASE:
                activity = buildUserBattleActivity(kosProfile, userBase, (UserBase) destination, lineUp, missionType);
                break;
            case RESOURCE:
                activity = buildMiningBattleActivity(kosProfile, userBase, (ResourceIsland) destination, lineUp, missionType);
                break;
            case BOSS:
                activity = buildBossBattleActivity(kosProfile, userBase, (BossSea) destination, lineUp, missionType);
                break;
            default:
                activity = buildActivity(kosProfile, userBase, destination, lineUp, missionType);
        }
        MoveSession activeMoveSession = activity.getActiveMoveSession();
        moveSessionService.attachMoveSession(activity, activeMoveSession);
        updateCurrentActionStatus(activity,
                                  LocalDateTime.now().plus(activeMoveSession.getDurationInMillis(), ChronoUnit.MILLIS),
                                  SeaActivityStatus.MOVING);
        return activity;
    }

    @Transactional
    public List<SeaActivity> findByElementIdAndKosProfileId(Long elementId, Long kosProfileId) {
        return repository.findByElementIdAndKosProfileId(elementId, kosProfileId);
    }

    private void eligibleToStartActivity(KosProfile kosProfile, Long destinationId, MissionType missionType, ShipLineUp lineUp) {
        if (getAvailableActionPoints(kosProfile.getId()) == 0) {
            if (Objects.isNull(lineUp) || Objects.isNull(lineUp.getActivity())) {
                throw KOSException.of(ErrorCode.NOT_ENOUGH_ACTION_POINT);
            }
        }
        UserBase userBase = userBaseService.getByKosProfileId(kosProfile.getId());
        SeaElement destination = seaElementService.getElementById(destinationId);
        if (destination.getId().equals(userBase.getId())) {
            throw new KOSException(ErrorCode.INVALID_DESTINATION);
        }
        if (!missionType.equals(MissionType.STATION)) {
            if (moveSessionService.existsInProgressMoveWithMissionToTarget(missionType, destination.getId(), kosProfile.getId()).equals(
                    Boolean.TRUE)) {
                throw KOSException.of(ErrorCode.ALREADY_SENT_TROOP_ON_ATTACKING_MISSION);
            }
        }

    }

    private void sendAttackWarningScreenPrompt(Collection<KosProfile> defenders, Coordinates battleSite) {
        for (KosProfile defender : defenders) {
            seaActivityAsyncTask.sendAttackNotification(defender.getUser().getId(), battleSite);
        }
    }

    private SeaActivity buildUserBattleActivity(KosProfile kosProfile,
                                                UserBase source,
                                                UserBase destination,
                                                ShipLineUp shipLineUp,
                                                MissionType missionType) {

        try {
            militaryBuildingService.getBuildingInfo(new GetMilitaryBuildingInfo(destination.getKosProfile().getId()));
            commandBuildingService.getBuildingInfo(new GetCommandBuildingInfo(destination.getKosProfile().getId()));
        } catch (KOSException exception) {
            if (exception.getCode().equals(ErrorCode.BUILDING_IS_LOCKED)) {
                throw KOSException.of(ErrorCode.PEACE_SHIELD_ACTIVATED);
            } else {throw exception;}
        }

        if (Objects.isNull(destination.getKosProfile())) {
            throw KOSException.of(ErrorCode.ELEMENT_NOT_FOUND);
        }

        Battle battleOnSite = destination.getBattle();
        SeaActivity activity;
        if (Objects.isNull(battleOnSite)) {
            Invader invader = destination.getInvader();
            if (Objects.isNull(invader)) {
                if (missionType.equals(MissionType.ATTACK) || missionType.equals(MissionType.OCCUPY)) {
                    activity = buildActivity(kosProfile, source, destination, shipLineUp, missionType);
                    sendAttackWarningScreenPrompt(List.of(destination.getKosProfile()), destination.getCoordinates());
                } else if (missionType.equals(MissionType.STATION)) {
                    throw KOSException.of(ErrorCode.CAN_NOT_STATION_ON_TARGET);
                } else {
                    throw KOSException.of(ErrorCode.MISSION_TYPE_NOT_FOUND);
                }
            } else {
                switch (missionType) {
                    case ATTACK:
                        throw KOSException.of(ErrorCode.MISSION_TYPE_NOT_SUPPORTED);
                    case OCCUPY:
                        if (invader.getKosProfileInvader().getId().equals(kosProfile.getId())) {
                            throw KOSException.of(ErrorCode.MISSION_TYPE_NOT_SUPPORTED);
                        }
                        activity = buildActivity(kosProfile, source, destination, shipLineUp, missionType);
                        activity.getActiveMoveSession().setKosTargetId(invader.getKosProfileInvader().getId());
                        sendAttackWarningScreenPrompt(List.of(destination.getKosProfile()), destination.getCoordinates());
                        break;
                    case STATION:
                        if (!occupyService.occupiedByAlliance(kosProfile, destination)) {
                            throw KOSException.of(ErrorCode.CAN_NOT_STATION_ON_TARGET);
                        }
                        activity = buildActivity(kosProfile, source, destination, shipLineUp, missionType);
                        break;
                    default:
                        throw KOSException.of(ErrorCode.MISSION_TYPE_NOT_FOUND);
                }

            }
        } else {
            //if user base had battle, check and join to battle
            BattleStatus battleStatus = battleOnSite.getStatus();
            if (battleStatus.equals(BattleStatus.PROGRESS) || battleStatus.equals(BattleStatus.BREAK)) {
                switch (battleOnSite.getBattleType()) {
                    case OCCUPY:
                    case LIBERATE:
                        missionType = MissionType.OCCUPY;
                        break;
                    case ATTACK:
                        missionType = MissionType.ATTACK;
                        break;
                }
                activity = buildActivity(kosProfile, source, destination, shipLineUp, missionType);
                activity.getActiveMoveSession().setBattleId(battleOnSite.getId());

            } else {
                throw KOSException.of(ErrorCode.CAN_NOT_JOIN_BATTLE);
            }
        }
        return activity;
    }

    /**
     * Setup station for activity
     * and add it init list element's activity
     */
    @Transactional
    public void stationOnBase(SeaActivity activity, SeaElement element) {
        updateCurrentActionStatus(activity, null, SeaActivityStatus.OCCUPYING);
        activity.setStationAt(element);
        element.getSeaActivities().add(activity);
        repository.save(activity);
    }

    private SeaActivity buildMiningBattleActivity(KosProfile kosProfile,
                                                  SeaElement source,
                                                  ResourceIsland destination,
                                                  ShipLineUp shipLineUp,
                                                  MissionType missionType) {
        Battle battle = destination.getBattle();
        if (Objects.nonNull(battle)) {
            throw KOSException.of(ErrorCode.CAN_NOT_JOIN_BATTLE);
        }
        SeaActivity activity = buildActivity(kosProfile, source, destination, shipLineUp, missionType);
        if (Objects.nonNull(destination.getMiningSession())) {
            activity.getActiveMoveSession().setKosTargetId(destination.getMiningSession().getSeaActivity().getKosProfile().getId());
            sendAttackWarningScreenPrompt(List.of(destination.getMiningSession().getSeaActivity().getKosProfile()), destination.getCoordinates());
        }
        return activity;
    }

    private SeaActivity buildBossBattleActivity(KosProfile kosProfile, UserBase source, BossSea destination, ShipLineUp shipLineUp,
                                                MissionType missionType) {
        Battle battle = destination.getBattle();
        SeaActivity activity;
        if (Objects.isNull(battle)) {
            // if user base didn't had battle, create battle
            activity = buildActivity(kosProfile, source, destination, shipLineUp, missionType);
        } else {
            throw KOSException.of(ErrorCode.BOSS_IS_ATTACKED);
        }
        return activity;
    }

    private SeaActivity buildActivity(KosProfile kosProfile, SeaElement source, SeaElement destination, ShipLineUp shipLineUp,
                                      MissionType missionType) {
        if (Objects.isNull(missionType)) {
            throw KOSException.of(ErrorCode.MISSION_TYPE_NOT_FOUND);
        }
        MoveSession moveSession;
        SeaActivity activity;
        if (Objects.nonNull(shipLineUp) && Objects.nonNull(shipLineUp.getActivity())) {
            if (!shipLineUp.getActivity().getStatus().equals(SeaActivityStatus.OCCUPYING) || Objects.isNull(
                    shipLineUp.getActivity().getStationAt())) {
                throw KOSException.of(ErrorCode.SOURCE_IS_NOT_ANCHOR);
            }
            activity = shipLineUp.getActivity();
            moveSession = moveSessionService.buildMoveSession(activity.getCurrentLocation(), activity.getStationAt().getId(),
                                                              destination.getCoordinates(),
                                                              destination.getId(), destination.type(), missionType);
            activity.setActiveMoveSession(moveSession);
            withdrawHandlerFactory.getInstance(source.getSeaElementConfig().getType()).cleanUpOnWithdraw(activity.getStationAt(), activity);
            moveSession.setSeaActivity(activity);
            repository.save(activity);
            return activity;
        }
        activity = new SeaActivity();
        moveSession = moveSessionService.buildMoveSession(source.getCoordinates(), source.getId(), destination.getCoordinates(),
                                                          destination.getId(), destination.type(), missionType);
        activity = repository.save(activity);
        activity.setKosProfile(kosProfile);
        if (Objects.nonNull(shipLineUp)) {
            lineUpService.attachToActivity(activity, shipLineUp);
            activity.setSpeed(lineUpService.getLineUpBaseSpeed(shipLineUp));
        }
        activity.setActiveMoveSession(moveSession);
        moveSession.setSeaActivity(activity);
        repository.save(activity);
        return activity;
    }

    public SeaElement findElementToReturn(Long id) {
        SeaActivity activity = repository.findById(id).orElse(null);
        if (Objects.isNull(activity)) {
            return null;
        }
        return findElementToReturn(activity);

    }

    public SeaElement findElementToReturn(SeaActivity activity) {
        MoveSession prevMove = activity.getActiveMoveSession();
        UserBase userBase = userBaseService.getByKosProfileId(activity.getKosProfile().getId());
        if (prevMove.getMissionType().equals(MissionType.RETURN)) {
            return userBase;
        }
        Long sourceElementId = prevMove.getSourceElementId();
        Long destinationElementId = prevMove.getDestinationElementId();
        SeaElement sourceElement = seaElementService.findElementById(sourceElementId);
        if (Objects.isNull(sourceElement)
            || sourceElement.getId().equals(userBase.getId())
            || !occupyService.occupiedByAlliance(activity.getKosProfile(), sourceElement)) {
            return userBase;
        } else {
            return sourceElement;
        }
    }

    /**
     * Withdraw activity
     */
    @Transactional
    public SeaActivity withdraw(WithdrawActivityCommand command) {
        SeaActivity activity = findById(command.getId());
        if (Boolean.TRUE.equals(activity.getIsDeleted())) {
            return activity;
        }

        MoveSession prevMove = activity.getActiveMoveSession();
        if (Objects.isNull(activity.getCurrentLocation()) && prevMove.getMissionType().equals(MissionType.RETURN)) {
            return activity;
        }
        MoveSession goBackMove;
        Coordinates currentPosition;
        if (Objects.nonNull(activity.getCurrentLocation())) {
            currentPosition = activity.getCurrentLocation();
        } else {
            // on the way to target
            prevMove.setWithdrawnTime(LocalDateTime.now());
            long expectedTravellingDuration = (long) (DistanceUtils.getDistance(prevMove.getStart(),
                                                                                prevMove.getEnd()) / prevMove.getSpeed());
            long elapsedTime = ChronoUnit.SECONDS.between(prevMove.getTimeStart(), prevMove.getWithdrawnTime());
            currentPosition = SeaMapCoordinatesUtils.getCurrentLocation(prevMove.getStart(),
                                                                        prevMove.getEnd(),
                                                                        prevMove.getSpeed(),
                                                                        Math.min(elapsedTime, expectedTravellingDuration));
            seaActivityAsyncTask.sendCancelMoveSessionNotification(prevMove.getSeaActivity().getKosProfile().getUser());
        }

        SeaElement elementToReturn = findElementToReturn(activity);
        var handler = withdrawHandlerFactory.getInstance(activity.getActiveMoveSession().getDestinationType());
        SeaElement prevDestinationElement = seaElementService.findElementById(prevMove.getDestinationElementId());
        if (Objects.isNull(prevDestinationElement)) {
            goBackMove = moveSessionService.buildMoveSession(currentPosition,
                                                             null,
                                                             elementToReturn.getCoordinates(),
                                                             elementToReturn.getId(),
                                                             elementToReturn.getSeaElementConfig().getType(),
                                                             MissionType.RETURN);
        } else {
            handler.cleanUpOnWithdraw(prevDestinationElement, activity);
            goBackMove = moveSessionService.buildMoveSession(currentPosition,
                                                             prevDestinationElement.getId(),
                                                             elementToReturn.getCoordinates(),
                                                             elementToReturn.getId(),
                                                             elementToReturn.getSeaElementConfig().getType(),
                                                             MissionType.RETURN);
        }

        activity.setCurrentLocation(null);
        moveSessionService.attachMoveSession(activity, goBackMove);
        var activeMoveSession = activity.getActiveMoveSession();
        updateCurrentActionStatus(activity,
                                  LocalDateTime.now().plus(activeMoveSession.getDurationInMillis(), ChronoUnit.MILLIS),
                                  SeaActivityStatus.MOVING);
        return repository.save(activity);
    }

    public SeaActivity updateCurrentActionStatus(SeaActivity activity, LocalDateTime endTime, SeaActivityStatus status) {
        ShipLineUp lineUp = activity.getLineUp();
        if (Objects.nonNull(lineUp)) {
            MotherShip motherShip = lineUp.getMotherShip();
            motherShip.setStatus(status);

            motherShipServiceAsyncTask.sendHealingNotification(lineUp.getMotherShip());
        }
        activity.setTimeEnd(endTime);
        activity.setStatus(status);
        return save(activity);
    }

    public SeaActivity save(SeaActivity activity) {
        repository.save(activity);
        return activity;
    }

    public List<SeaActivity> findActiveActivity(Long kosProfileId) {
        return repository.findActiveActivities(kosProfileId);
    }

    public Long getAvailableActionPoints(Long kosProfileId) {
        return getMaximumActionAllowed(kosProfileId) - countActiveActivities(kosProfileId);
    }

    public Long getMaximumActionAllowed(Long kosProfileId) {
        LighthouseBuilding lightHouseBuilding = lightHouseBuildingService.getBuildingInfo(kosProfileId);
        return lightHouseBuilding.getMaxActionPoint();
    }

    public Long countActiveActivities(Long kosProfileId) {
        return repository.countActiveActivities(kosProfileId);
    }

    public List<SeaActivity> getAllMovingActivity() {
        return repository.findMovingActivity();
    }

    public SeaActivity findById(Long id) {
        SeaActivity seaActivity = repository.findById(id).orElseThrow(() -> new KOSException(ErrorCode.SEA_ACTIVITY_NOT_FOUND));
        return seaActivity;
    }

    public SeaActivity startScoutActivity(Scout scout) {
        eligibleToStartActivity(scout.getScouter(), scout.getSeaElement().getId(), MissionType.SCOUT, null);
        SeaActivity activity = buildActivity(scout.getScouter(), scout.getScouter().getBase(), scout.getSeaElement(), null, MissionType.SCOUT);
        scoutRepository.save(scout);
        activity.setScout(scout);
        activity.setSpeed(scout.getSpeed());
        moveSessionService.attachMoveSession(activity, activity.getActiveMoveSession());
        double travellingDuration = scout.getMissionTime() / 2.0;
        updateCurrentActionStatus(activity, LocalDateTime.now().plus((long) (travellingDuration * 1000), ChronoUnit.MILLIS),
                                  SeaActivityStatus.SCOUTING);
        repository.save(activity);
        return activity;
    }

    public void anchorShip(List<SeaActivity> seaActivities, SeaElement element) {
        List<SeaActivity> seaActivitiesAnchor = new ArrayList<>();
        List<SeaActivity> seaActivitiesWithdraw = new ArrayList<>();
        for (SeaActivity seaActivity : seaActivities) {
            if (seaActivity.getLineUp().getMotherShip().getCurrentHp() > 0) {
                stationOnBase(seaActivity, element);
                seaActivitiesAnchor.add(seaActivity);
            } else {
                seaActivitiesWithdraw.add(seaActivity);
            }
        }
        seaActivitiesWithdraw.forEach(seaActivity -> withdraw(new WithdrawActivityCommand().setId(seaActivity.getId())));
        repository.saveAll(seaActivitiesAnchor);
    }

    public List<SeaActivity> getListOccupyInElement(Long seaElementId) {
        return repository.findByStationAtAndStatus(seaElementId, SeaActivityStatus.OCCUPYING);
    }
}
