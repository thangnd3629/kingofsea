package com.supergroup.kos.building.domain.service.upgrade;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supergroup.auth.domain.model.User;
import com.supergroup.auth.domain.service.UserService;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.UpgradeServiceAsyncTask;
import com.supergroup.kos.building.domain.command.GetUpgradeStatusCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.SaveOrUpdateElementCommand;
import com.supergroup.kos.building.domain.command.UpgradeBuildingCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.building.BaseBuilding;
import com.supergroup.kos.building.domain.model.building.CastleBuilding;
import com.supergroup.kos.building.domain.model.building.StorageBuildingConfig;
import com.supergroup.kos.building.domain.model.config.ArmoryBuildingConfig;
import com.supergroup.kos.building.domain.model.config.BaseBuildingConfig;
import com.supergroup.kos.building.domain.model.config.CommandBuildingConfig;
import com.supergroup.kos.building.domain.model.config.CommunityBuildingConfig;
import com.supergroup.kos.building.domain.model.config.LighthouseBuildingConfig;
import com.supergroup.kos.building.domain.model.config.MilitaryBuildingConfig;
import com.supergroup.kos.building.domain.model.config.QueenBuildingConfig;
import com.supergroup.kos.building.domain.model.config.ScoutBuildingConfig;
import com.supergroup.kos.building.domain.model.config.StoneMineConfig;
import com.supergroup.kos.building.domain.model.config.VaultBuildingConfig;
import com.supergroup.kos.building.domain.model.config.WoodMineConfig;
import com.supergroup.kos.building.domain.model.mining.QueenBuilding;
import com.supergroup.kos.building.domain.model.point.Point;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.upgrade.InfoInstanceModel;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.repository.persistence.asset.AssetsRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.point.PointRepository;
import com.supergroup.kos.building.domain.service.building.CastleBuildingService;
import com.supergroup.kos.building.domain.service.building.ResearchBuildingService;
import com.supergroup.kos.building.domain.service.point.PointService;
import com.supergroup.kos.building.domain.service.seamap.MapService;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;
import com.supergroup.kos.building.domain.service.technology.TechnologyService;
import com.supergroup.kos.building.domain.task.UpgradeTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author idev
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UpgradeService {

    private static final int THRESHOLD = 2000; // mili

    private final BuildingRepository       buildingRepository;
    private final BuildingConfigDataSource buildingConfigDataSource;
    private final UpgradeServiceAsyncTask  upgradeServiceAsyncTask;
    private final PointRepository          pointRepository;
    private final AssetsRepository         assetsRepository;
    private final ObjectMapper             objectMapper;
    private final RabbitTemplate           rabbitTemplate;
    private final CastleBuildingService    castleBuildingService;

    private final ScheduledExecutorService scheduledExecutorService;
    private final ResearchBuildingService  researchBuildingService;
    private final UpgradeSessionService    upgradeSessionService;
    private final PointService             pointService;
    private final UserService              userService;
    private final TechnologyService        technologyService;
    private final MapService               mapService;
    private final SeaElementService        seaElementService;

    /**
     * Upgrade building to next level
     */
    @Transactional
    public void upgrade(UpgradeBuildingCommand command) {
        log.info("Upgrade {} building of profile {}", command.getBuilding().getName().name(), command.getBuilding().getKosProfile().getId());
        try {
            var config = buildingConfigDataSource.getConfig(command.getBuilding().getName(), command.getBuilding().getLevel() + 1);

            // reduce upgrading time
            var reducePercent = command.getKosProfile().getReduceUpgradingTimePercent();
            config.setUpgradeDuration(Double.valueOf(config.getUpgradeDuration() * (1 - reducePercent)).longValue());

            if (!checkUpgradeCondition(command.getBuilding(), config)) {
                throw KOSException.of(ErrorCode.NOT_ELIGIBLE_FOR_UPGRADE);
            }

            if (!checkResourceRequirement(config, command.getAssets())) {
                throw KOSException.of(ErrorCode.DO_NOT_MEET_RESOURCE_REQUIREMENT);
            }
            if (!buildingIsAvailable(command.getBuilding())) {
                throw KOSException.of(ErrorCode.BUILDING_IS_IN_OTHER_PROCESS);
            }

            command.getBuilding().validUnlockBuilding(technologyService);

            takeAssetToUpgrade(config, command.getAssets());
            startUpgradeProcess(command.getBuilding(), config);
        } catch (KOSException kosException) {
            if (kosException.getCode().equals(ErrorCode.CONFIG_NOT_FOUND)) {
                throw KOSException.of(ErrorCode.CAN_NOT_UPGRADE_BECAUSE_NOT_FOUND_NEXT_LEVEL);
            } else {throw kosException;}
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw KOSException.of(ErrorCode.SERVER_ERROR);
        }
    }

    /**
     * Level up building and claim reward after upgrade
     */
    @Transactional
    public void validUpgrading(User user, BaseBuilding building, BaseBuildingConfig config, Point point) {
        log.info("Valid upgrading {} of profile {}", building.getName().name(), building.getKosProfile().getId());
        completeUpgrading(building, config);
        claimRewardAfterUpgrade(config, point);
        log.info("Send notification to user id {}", user.getId());
        scheduledExecutorService.schedule(() -> {
            upgradeServiceAsyncTask.sendUpgradeNotification(user.getId(), building.getName(), config.getLevel());
        }, THRESHOLD, TimeUnit.MILLISECONDS);
    }

    public UpgradeSession getUpgradeSession(GetUpgradeStatusCommand command) {
        BaseBuilding building = buildingRepository.get(command.getBuildingName(), command.getKosProfileId());
        if (Objects.nonNull(building.getUpgradeSession())) {
            return building.getUpgradeSession();
        } else {
            throw KOSException.of(ErrorCode.BUILDING_NOT_IN_UPGRADING);
        }

    }

    private void completeUpgrading(BaseBuilding building, BaseBuildingConfig config) {
        if (building instanceof QueenBuilding) {
            var queenCardReward = ((QueenBuildingConfig) config).getQueenCardReward();
            if (queenCardReward != null) {
                ((QueenBuilding) building).setNumberOfQueenCard(
                        ((QueenBuilding) building).getNumberOfQueenCard() + queenCardReward);
            }
        }
        if (building instanceof CastleBuilding) {
            try {
                castleBuildingService.claimPeopleAndGold(building.getKosProfile().getId());
            } catch (Exception ex) {
                // ignore
                ex.printStackTrace();
            }
        }
        Long upgradeSessionId = building.getUpgradeSession() != null ? building.getUpgradeSession().getId() : null;
        building.setUpgradeSession(null);
        building.setLevel(building.getLevel() + 1); // up to next level
        // save building state
        buildingRepository.save(building);
        if (upgradeSessionId != null) {
            upgradeSessionService.deleteById(upgradeSessionId);
        }
        // update level base to cache
        if (building instanceof CastleBuilding) {
            var userBase = seaElementService.findUserBaseByKosProfileIdFromDatabase(building.getKosProfile().getId());
            userBase.getKosProfile().setLevel(building.getLevel());
            mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(userBase));
        }
    }

    private Boolean isDone(BaseBuilding building) {
        if (Objects.isNull(building.getUpgradeSession())) {
            return true;
        }
        UpgradeSession upgradeSession = building.getUpgradeSession();
        return Objects.nonNull(upgradeSession.getTimeStart()) && Objects.nonNull(upgradeSession.getDuration())
               && upgradeSession.getTimeStart().plus(upgradeSession.getDuration() - THRESHOLD, ChronoUnit.MILLIS).isBefore(LocalDateTime.now());
    }

    private void claimRewardAfterUpgrade(BaseBuildingConfig config, Point point) {
        point.setGpPoint(point.getGpPoint() + config.getGpPointReward());
        pointRepository.save(point);
    }

    private void startUpgradeProcess(BaseBuilding building, BaseBuildingConfig baseBuildingConfig) throws JsonProcessingException {
        // give building into upgrade process
        log.info("Start upgrading {} {}", building.getName(), building);
        UpgradeSession upgradeSession = getUpgradeSession(building, baseBuildingConfig);
        building.setUpgradeSession(upgradeSession);
        // save building state
        buildingRepository.save(building);

        var task = new UpgradeTask().setUpgradeSessionId(upgradeSession.getId());
        log.info("Send to queue");
        var duration = Double.valueOf(baseBuildingConfig.getUpgradeDuration() * (1 - building.getKosProfile().getReduceUpgradingTimePercent()));
        sendUpgradeTaskToQueue(task, duration.longValue());
    }

    public void sendUpgradeTaskToQueue(UpgradeTask task, Long duration) throws JsonProcessingException {
        var taskJson = objectMapper.writeValueAsString(task);
        var prop = new MessageProperties();
        prop.setHeader("x-delay", duration - THRESHOLD);
        var mess = MessageBuilder.withBody(taskJson.getBytes())
                                 .andProperties(prop)
                                 .build();
        rabbitTemplate.convertAndSend("upgrading-exchange", "upgrading", mess);
    }

    private Boolean checkResourceRequirement(BaseBuildingConfig baseBuildingConfig, Assets assets) {
        return assets.getWood() >= baseBuildingConfig.getWood()
               && assets.getGold() >= baseBuildingConfig.getGold()
               && assets.getStone() >= baseBuildingConfig.getStone();
    }

    private Boolean buildingIsAvailable(BaseBuilding building) {
        return (Objects.isNull(building.getUpgradeSession()) && !building.getIsLock());
    }

    private void takeAssetToUpgrade(BaseBuildingConfig baseBuildingConfig, Assets assets) {
        assets.setGold(assets.getGold() - baseBuildingConfig.getGold());
        assets.setStone(assets.getStone() - baseBuildingConfig.getStone());
        assets.setWood(assets.getWood() - baseBuildingConfig.getWood());
        assetsRepository.save(assets);
    }

    private Boolean checkUpgradeCondition(BaseBuilding building, BaseBuildingConfig config) {
        Long kosProfileId = building.getKosProfile().getId();
        BaseBuilding buildingCondition;
        switch (building.getName()) {
            case WOOD_MINE:
                buildingCondition = researchBuildingService.getByKosProfileId(building.getKosProfile().getId());
                return (buildingCondition.getLevel() >= ((WoodMineConfig) config).getResearchLevelRequired());
            case STONE_MINE:
                buildingCondition = researchBuildingService.getByKosProfileId(building.getKosProfile().getId());
                return (buildingCondition.getLevel() >= ((StoneMineConfig) config).getResearchLevelRequired());
            case STORAGE_WOOD:
            case STORAGE_STONE:
            case STORAGE_GOLD:
                buildingCondition = castleBuildingService.getCastleBuilding(new KosProfileCommand().setKosProfileId(kosProfileId));
                return (buildingCondition.getLevel() >= ((StorageBuildingConfig) config).getLevelHeadquarters());
            case SCOUT:
                buildingCondition = castleBuildingService.getCastleBuilding(new KosProfileCommand().setKosProfileId(kosProfileId));
                return (buildingCondition.getLevel() >= ((ScoutBuildingConfig) config).getCastleLevelRequired());
            case ARMORY:
                buildingCondition = researchBuildingService.getByKosProfileId(building.getKosProfile().getId());
                return (buildingCondition.getLevel() >= ((ArmoryBuildingConfig) config).getResearchLevelRequired());
            case COMMAND:
                buildingCondition = researchBuildingService.getByKosProfileId(building.getKosProfile().getId());
                return (buildingCondition.getLevel() >= ((CommandBuildingConfig) config).getResearchLevelRequired());
            case COMMUNITY:
                buildingCondition = castleBuildingService.getCastleBuilding(new KosProfileCommand().setKosProfileId(kosProfileId));
                return (buildingCondition.getLevel() >= ((CommunityBuildingConfig) config).getCastleLevelRequired());
            case VAULT:
                buildingCondition = researchBuildingService.getByKosProfileId(building.getKosProfile().getId());
                return (buildingCondition.getLevel() >= ((VaultBuildingConfig) config).getResearchLevelRequired());
            case MILITARY:
                buildingCondition = researchBuildingService.getByKosProfileId(building.getKosProfile().getId());
                return (buildingCondition.getLevel() >= ((MilitaryBuildingConfig) config).getResearchLevelRequired());
            case LIGHTHOUSE:
                buildingCondition = castleBuildingService.getCastleBuilding(new KosProfileCommand().setKosProfileId(kosProfileId));
                return (buildingCondition.getLevel() >= ((LighthouseBuildingConfig) config).getLevelHeadquarter());
            default:
                return true;
        }

    }

    private UpgradeSession getUpgradeSession(BaseBuilding building, BaseBuildingConfig baseBuildingConfig) {
        return upgradeSessionService.createUpgradeBuildingSession(building, LocalDateTime.now(),
                                                                  baseBuildingConfig.getUpgradeDuration());

    }

    @Transactional
    public void completeUpgradeBuilding(UpgradeSession upgradeSession) {
        upgradeSession.setIsDeleted(true);
        upgradeSessionService.save(upgradeSession);
        InfoInstanceModel infoInstanceModel = upgradeSession.getInfoInstanceModel();
        KosProfile kosProfile = upgradeSession.getKosProfile();
        BuildingName buildingName = infoInstanceModel.getBuildingName();
        var building = buildingRepository.get(buildingName, kosProfile.getId());
        var config = buildingConfigDataSource.getConfig(buildingName, building.getLevel() + 1);
        var point = pointService.findByKosProfile_Id(kosProfile.getId())
                                .orElseThrow(() -> KOSException.of(ErrorCode.KOS_POINTS_NOT_FOUND));
        var user = userService.findById(kosProfile.getUser().getId())
                              .orElseThrow(() -> KOSException.of(ErrorCode.USER_NOT_FOUND));
        validUpgrading(user, building, config, point);
    }

}
