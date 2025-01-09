package com.supergroup.kos.building.domain.service.ship;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.EscortShipServiceAsyncTask;
import com.supergroup.kos.building.domain.async.ShipUpgradeAsyncTask;
import com.supergroup.kos.building.domain.command.BuildEscortShipCommand;
import com.supergroup.kos.building.domain.command.GetEscortShipCommand;
import com.supergroup.kos.building.domain.command.GetMilitaryBuildingInfo;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.QueueEscortShipCommand;
import com.supergroup.kos.building.domain.command.UpgradeEscortShipCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.EscortShipGroupName;
import com.supergroup.kos.building.domain.constant.UpgradeType;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;
import com.supergroup.kos.building.domain.model.config.EscortShipConfig;
import com.supergroup.kos.building.domain.model.config.EscortShipLevelConfig;
import com.supergroup.kos.building.domain.model.config.MilitaryBuildingConfig;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.ship.EscortShip;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipLevelConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipRepository;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.battle.UpdateBattleUnitService;
import com.supergroup.kos.building.domain.service.building.MilitaryBuildingService;
import com.supergroup.kos.building.domain.service.config.KosConfigService;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;
import com.supergroup.kos.building.domain.service.technology.UserTechnologyService;
import com.supergroup.kos.building.domain.service.upgrade.UpgradeSessionService;
import com.supergroup.kos.building.domain.task.UpgradeTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EscortShipService {
    private static final int THRESHOLD = 2000; // mili

    private final AssetsService                   assetsService;
    private final UpgradeSessionService           upgradeSessionService;
    private final UserTechnologyService           userTechnologyService;
    private final EscortShipRepository            escortShipRepository;
    private final EscortShipLevelConfigDataSource escortShipLevelConfigDataSource;
    private final ObjectMapper                    objectMapper;
    private final RabbitTemplate                  rabbitTemplate;
    private final MilitaryBuildingService         militaryBuildingService;
    private final BuildingConfigDataSource        buildingConfigDataSource;
    private final EscortShipServiceAsyncTask      escortShipServiceAsyncTask;
    private final ShipUpgradeAsyncTask            shipUpgradeAsyncTask;
    private final KosConfigService                kosConfigService;
    private final UserBaseService                 userBaseService;
    private final UpdateBattleUnitService         updateBattleUnitService;

    public EscortShip getEscortShipByShipType(GetEscortShipCommand command) {
        return escortShipRepository.findByKosProfileIdAndShipType(command.getKosProfileId(), command.getShipType())
                                   .orElseThrow(() -> KOSException.of(ErrorCode.ESCORT_SHIP_IS_NOT_FOUND));
    }

    public EscortShip findById(Long escortShipId) {
        return escortShipRepository.findById(escortShipId).orElseThrow(()-> KOSException.of(ErrorCode.ESCORT_SHIP_IS_NOT_FOUND));
    }

    public List<EscortShip> getEscortShips(Long kosProfileId) {
        return escortShipRepository.findByKosProfileId(kosProfileId);
    }

    public List<EscortShip> getEscortShipsBuildingOrQueueing(Long kosProfileId) {
        return escortShipRepository.findByEscortShipBuildingOrQueueing(kosProfileId);
    }

    public List<EscortShip> getEscortShipsByGroupShip(Long kosProfileId, EscortShipGroupName groupName) {
        return escortShipRepository.findEscortShipsGroupShip(kosProfileId, groupName);
    }

    public Long roundingTime(Long timeBase, Double discountPercent) {
        return Math.round(timeBase / (discountPercent * 1000)) * 1000;
    }

    /** Upgrade level escort ship */
    @Transactional
    public void upgradeEscortShip(UpgradeEscortShipCommand command) {
        try {
            var escortShip = getEscortShipByShipType(new GetEscortShipCommand()
                                                             .setShipType(command.getType())
                                                             .setKosProfileId(command.getKosProfileId()));
            var escortShipLevelConfig = escortShipLevelConfigDataSource.getByTypeAndLevel(command.getType(), escortShip.getLevel() + 1);
            var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(command.getKosProfileId()));
            if (Objects.nonNull(escortShipLevelConfig.getTechnologyCodeRequirement())) {
                var ut = userTechnologyService.findByKosProfileIdAndTechnologyCode(escortShipLevelConfig.getTechnologyCodeRequirement(),
                                                                                   command.getKosProfileId());
                if (Objects.isNull(ut.getIsResearched()) || ut.getIsResearched().equals(false)) {
                    throw KOSException.of(ErrorCode.CAN_NOT_UPGRADE_BECAUSE_THE_TECHNOLOGY_IS_NOT_RESEARCHED);
                }
            }
            if (!checkResourceUpgradeRequirement(escortShipLevelConfig, asset)) {
                throw KOSException.of(ErrorCode.DO_NOT_MEET_RESOURCE_REQUIREMENT);
            }
            if (!checkEscortShipUpgradeStatus(escortShip)) {
                throw KOSException.of(ErrorCode.ESCORT_SHIP_IS_IN_OTHER_PROCESS);
            }
            takeAssetToUpgrade(escortShipLevelConfig, asset);
            startUpgradeProcess(escortShip, escortShipLevelConfig);
        } catch (KOSException kosException) {
            if (kosException.getCode().equals(ErrorCode.CONFIG_NOT_FOUND)) {
                throw KOSException.of(ErrorCode.CAN_NOT_UPGRADE_BECAUSE_NOT_FOUND_NEXT_LEVEL);
            } else {throw kosException;}
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw KOSException.of(ErrorCode.SERVER_ERROR);
        }
    }

    private Boolean checkResourceUpgradeRequirement(EscortShipLevelConfig escortShipLevelConfig, Assets assets) {
        return assets.getWood() >= escortShipLevelConfig.getWood()
               && assets.getGold() >= escortShipLevelConfig.getGold()
               && assets.getStone() >= escortShipLevelConfig.getStone();
    }

    private Boolean checkEscortShipUpgradeStatus(EscortShip escortShip) {
        return (Objects.isNull(escortShip.getUpgradeSession()));
    }

    private void takeAssetToUpgrade(EscortShipLevelConfig escortShipLevelConfig, Assets assets) {
        assets.setGold(assets.getGold() - escortShipLevelConfig.getGold());
        assets.setStone(assets.getStone() - escortShipLevelConfig.getStone());
        assets.setWood(assets.getWood() - escortShipLevelConfig.getWood());
        assetsService.save(assets);
    }

    private void startUpgradeProcess(EscortShip escortShip, EscortShipLevelConfig escortShipLevelConfig)
            throws JsonProcessingException {
        // give building into upgrade process
        UpgradeSession upgradeSession = upgradeSessionService.createEscortShipSession(escortShip, LocalDateTime.now(),
                                                                                      escortShipLevelConfig.getUpgradeDuration(),
                                                                                      UpgradeType.ESCORT_SHIP);
        escortShip.setUpgradeSession(upgradeSession);
        escortShipRepository.save(escortShip);
        var task = new UpgradeTask().setUpgradeSessionId(upgradeSession.getId());
        if (escortShipLevelConfig.getUpgradeDuration() - THRESHOLD <= 0) {
            completeUpgradeLevelEscortShip(upgradeSession);
        } else {
            log.info("Send to queue");
            sendUpgradeTaskToQueue(task, escortShipLevelConfig.getUpgradeDuration() - THRESHOLD);
        }
    }

    private void sendUpgradeTaskToQueue(UpgradeTask task, Long delay) throws JsonProcessingException {
        var taskJson = objectMapper.writeValueAsString(task);
        var prop = new MessageProperties();
        prop.setHeader("x-delay", delay);
        var mess = MessageBuilder.withBody(taskJson.getBytes())
                                 .andProperties(prop)
                                 .build();
        rabbitTemplate.convertAndSend("upgrading-exchange", "upgrading", mess);

    }

    @Transactional
    public void completeUpgradeLevelEscortShip(UpgradeSession upgradeSession) {
        upgradeSession.setIsDeleted(true);
        upgradeSessionService.save(upgradeSession);
        var infoInstanceModel = upgradeSession.getInfoInstanceModel();
        var escortShip = escortShipRepository.findById(infoInstanceModel.getInstanceId()).orElseThrow(() -> KOSException.of(
                ErrorCode.ESCORT_SHIP_IS_NOT_FOUND));
        var currentLevelStat = escortShipLevelConfigDataSource.getByTypeAndLevel(escortShip.getEscortShipConfig().getType(),
                                                                                 escortShip.getLevel());
        escortShip.setLevel(escortShip.getLevel() + 1);
        var nextLevelStat = escortShipLevelConfigDataSource.getByTypeAndLevel(escortShip.getEscortShipConfig().getType(),
                                                                              escortShip.getLevel());
        Long upgradeSessionId = Objects.nonNull(escortShip.getUpgradeSession()) ? escortShip.getUpgradeSession().getId() : null;
        escortShip.setUpgradeSession(null);
        escortShipRepository.save(escortShip);
        if (Objects.nonNull(upgradeSessionId)) {
            upgradeSessionService.deleteById(upgradeSessionId);
        }
        shipUpgradeAsyncTask.sendGuardShipUpgradeLevelNotification(
                upgradeSession.getKosProfile().getUser().getId(),
                escortShip.getEscortShipConfig(),
                currentLevelStat, nextLevelStat);
    }

    /** End Upgrade */

    /** Build escort ship */
    @Transactional
    public void buildEscortShip(BuildEscortShipCommand command) {
        try {
            var escortShip = getEscortShipByShipType(new GetEscortShipCommand()
                                                             .setShipType(command.getType())
                                                             .setKosProfileId(command.getKosProfileId()));
            var militaryBuilding = militaryBuildingService.getBuildingInfo(new GetMilitaryBuildingInfo(command.getKosProfileId()));
            var militaryBuildingConfig = (MilitaryBuildingConfig) buildingConfigDataSource.getConfig(BuildingName.MILITARY,
                                                                                                     militaryBuilding.getLevel());
            var escortShips = getEscortShips(command.getKosProfileId());
            var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(command.getKosProfileId()));
            if (command.getIsCharged().equals(false)) {
                if (!checkResourceBuildRequirement(escortShip, asset, command.getAmount())) {
                    throw KOSException.of(ErrorCode.DO_NOT_MEET_RESOURCE_REQUIREMENT);
                }
            }
            if (!checkEscortShipBuildStatus(escortShips)) {
                throw KOSException.of(ErrorCode.CAN_NOT_BUILD_BECAUSE_OTHER_SHIP_IS_BUILDING);
            }
            if (command.getIsCharged().equals(false)) {
                takeAssetToBuild(escortShip, asset, command.getAmount());
            }
            var buildDuration = roundingTime(Math.round(escortShip.getEscortShipConfig().getBuildDuration() * escortShip.getPercentSpeedBuild()),
                                             militaryBuildingConfig.getPercentDurationBuildShip());
            startBuildProcess(escortShip, command.getAmount(), buildDuration);
        } catch (KOSException kosException) {
            if (kosException.getCode().equals(ErrorCode.CONFIG_NOT_FOUND)) {
                throw KOSException.of(ErrorCode.CAN_NOT_BUILD_BECAUSE_CONFIG_NOT_FOUND);
            } else {throw kosException;}
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw KOSException.of(ErrorCode.SERVER_ERROR);
        }
    }

    private Boolean checkResourceBuildRequirement(EscortShip escortShip, Assets assets, Long amount) {
        return assets.getWood() >= escortShip.getEscortShipConfig().getWood() * amount * escortShip.getPercentRssBuild()
               && assets.getGold() >= escortShip.getEscortShipConfig().getGold() * amount * escortShip.getPercentRssBuild()
               && assets.getStone() >= escortShip.getEscortShipConfig().getStone() * amount * escortShip.getPercentRssBuild();
    }

    private Boolean checkEscortShipBuildStatus(List<EscortShip> escortShips) {
        return escortShips.stream().allMatch(ship -> Objects.isNull(ship.getBuildSession()));
    }

    private void takeAssetToBuild(EscortShip escortShip, Assets assets, Long amount) {
        var kosProfile = assets.getKosProfile();
        var config = escortShip.getEscortShipConfig();
        assets.setGold(assets.getGold() - getGoldCostToBuild(kosProfile, config) * amount * escortShip.getPercentRssBuild());
        assets.setStone(assets.getStone() - getStoneCostToBuild(kosProfile, config) * amount * escortShip.getPercentRssBuild());
        assets.setWood(assets.getWood() - getWoodCostToBuild(kosProfile, config) * amount * escortShip.getPercentRssBuild());
        assetsService.save(assets);
    }

    public Double getGoldCostToBuild(KosProfile kosProfile, EscortShipConfig escortShipConfig) {
        var cost = escortShipConfig.getGold().doubleValue();
        // if base is occupied, increase cost
        if (kosProfile.getBase().isOccupied()) {
            var occupyEffect = kosConfigService.occupyEffect();
            cost += (cost * occupyEffect.getIncreaseUpgradeEscortShipCost());
        }
        return cost;
    }

    public Double getWoodCostToBuild(KosProfile kosProfile, EscortShipConfig escortShipConfig) {
        var cost = escortShipConfig.getWood().doubleValue();
        // if base is occupied, increase cost
        if (kosProfile.getBase().isOccupied()) {
            var occupyEffect = kosConfigService.occupyEffect();
            cost += (cost * occupyEffect.getIncreaseUpgradeEscortShipCost());
        }
        return cost;
    }

    public Double getStoneCostToBuild(KosProfile kosProfile, EscortShipConfig escortShipConfig) {
        var cost = escortShipConfig.getStone().doubleValue();
        // if base is occupied, increase cost
        if (kosProfile.getBase().isOccupied()) {
            var occupyEffect = kosConfigService.occupyEffect();
            cost += (cost * occupyEffect.getIncreaseUpgradeEscortShipCost());
        }
        return cost;
    }

    private void startBuildProcess(EscortShip escortShip, Long amount, Long buildDuration)
            throws JsonProcessingException {
        UpgradeSession buildSession = upgradeSessionService.createEscortShipSession(escortShip, LocalDateTime.now(),
                                                                                    buildDuration * amount,
                                                                                    UpgradeType.ESCORT_BUILDING);
        escortShip.setBuildSession(buildSession);
        escortShip.setNumberOfShipBuilding(amount);
        escortShip.setInBuildQueue(false);
        escortShip.setStartQueueTime(null);
        escortShipRepository.save(escortShip);
        var task = new UpgradeTask().setUpgradeSessionId(buildSession.getId());
        if (buildDuration * amount - THRESHOLD <= 0) {
            completeBuildEscortShip(buildSession);
        } else {
            log.info("Send to queue");
            sendUpgradeTaskToQueue(task, buildDuration * amount - THRESHOLD);
        }
    }

    public Optional<EscortShip> getNextEscortShipQueue(Long kosProfileId) {
        var escortShipQueueing = escortShipRepository.findByEscortShipQueueing(kosProfileId);
        if (escortShipQueueing.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(escortShipQueueing.get(0));
    }

    @Transactional
    public void completeBuildEscortShip(UpgradeSession upgradeSession) {
        upgradeSession.setIsDeleted(true);
        upgradeSessionService.save(upgradeSession);
        var infoInstanceModel = upgradeSession.getInfoInstanceModel();
        var kosProfile = upgradeSession.getKosProfile();
        var escortShip = escortShipRepository.findById(infoInstanceModel.getInstanceId()).orElseThrow(() -> KOSException.of(
                ErrorCode.ESCORT_SHIP_IS_NOT_FOUND));
        escortShipServiceAsyncTask.sendFinishBuildShipNotification(kosProfile.getUser().getId(), escortShip.getEscortShipConfig().getName());
        BattleProfile battleProfile = userBaseService.fightingOnMyBase(kosProfile.getId());
        if(Objects.nonNull(battleProfile)) {
            updateBattleUnitService.addEscortShipForBattleWhenCompleteBuild(battleProfile, escortShip, escortShip.getNumberOfShipBuilding());
        } else {
            escortShip.setAmount(escortShip.getAmount() + escortShip.getNumberOfShipBuilding());
        }
        escortShip.setNumberOfShipBuilding(null);
        escortShip.setInBuildQueue(null);
        escortShip.setBuildSession(null);
        escortShipRepository.save(escortShip);
        Long buildSessionId = Objects.nonNull(escortShip.getBuildSession()) ? escortShip.getBuildSession().getId() : null;
        if (Objects.nonNull(buildSessionId)) {
            upgradeSessionService.deleteById(buildSessionId);
        }
        var escortShipQueue = getNextEscortShipQueue(kosProfile.getId());
        escortShipQueue.ifPresent(ship -> buildEscortShip(new BuildEscortShipCommand()
                                                                  .setUserId(kosProfile.getUser().getId())
                                                                  .setKosProfileId(kosProfile.getId())
                                                                  .setType(escortShipQueue.get().getEscortShipConfig().getType())
                                                                  .setAmount(escortShipQueue.get().getNumberOfShipBuilding())
                                                                  .setIsCharged(true)));

    }
    /** End Build */

    /** Queue Ship */
    @Transactional
    public void queueBuildEscortShip(QueueEscortShipCommand command) {
        if (command.getIsQueueing().equals(false)) {
            cancelQueueBuildEscortShip(command);
        } else {
            addQueueBuildEscortShip(command);
        }
    }

    private Boolean checkBuildStatus(EscortShip escortShip) {
        return (Objects.isNull(escortShip.getBuildSession()));
    }

    private Boolean checkQueueStatus(EscortShip escortShip) {
        return (Objects.isNull(escortShip.getInBuildQueue()) || !escortShip.getInBuildQueue());
    }

    private Boolean checkAllShipBuildStatus(List<EscortShip> escortShips) {
        return escortShips.stream().allMatch(ship -> Objects.isNull(ship.getBuildSession()));
    }

    @Transactional
    public void cancelQueueBuildEscortShip(QueueEscortShipCommand command) {
        var escortShip = getEscortShipByShipType(new GetEscortShipCommand()
                                                         .setShipType(command.getType())
                                                         .setKosProfileId(command.getKosProfileId()));
        var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(command.getKosProfileId()));
        if (Objects.isNull(escortShip.getInBuildQueue()) || !escortShip.getInBuildQueue()) {
            throw KOSException.of(ErrorCode.ESCORT_SHIP_IS_NOT_IN_QUEUEING);
        }
        giveBackAsset(escortShip, asset);
        escortShip.setInBuildQueue(null)
                  .setStartQueueTime(null)
                  .setNumberOfShipBuilding(null);
        escortShipRepository.save(escortShip);
    }

    @Transactional
    public void addQueueBuildEscortShip(QueueEscortShipCommand command) {
        var escortShip = getEscortShipByShipType(new GetEscortShipCommand()
                                                         .setShipType(command.getType())
                                                         .setKosProfileId(command.getKosProfileId()));
        var escortShips = getEscortShips(command.getKosProfileId());
        var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(command.getKosProfileId()));
        if (Objects.isNull(command.getAmount())) {
            throw KOSException.of(ErrorCode.AMOUNT_ESCORT_SHIP_IS_NOT_NULL);
        }
        if (!checkResourceBuildRequirement(escortShip, asset, command.getAmount())) {
            throw KOSException.of(ErrorCode.DO_NOT_MEET_RESOURCE_REQUIREMENT);
        }
        if (checkAllShipBuildStatus(escortShips)) {
            throw KOSException.of(ErrorCode.CAN_NOT_QUEUE_BECAUSE_ALL_SHIP_IS_NOT_IN_BUILDING);
        }
        if (!checkBuildStatus(escortShip)) {
            throw KOSException.of(ErrorCode.ESCORT_SHIP_IS_BUILDING);
        }
        if (!checkQueueStatus(escortShip)) {
            throw KOSException.of(ErrorCode.ESCORT_SHIP_IS_QUEUEING);
        }
        takeAssetToBuild(escortShip, asset, command.getAmount());
        escortShip.setInBuildQueue(true);
        escortShip.setStartQueueTime(LocalDateTime.now());
        escortShip.setNumberOfShipBuilding(command.getAmount());
        escortShipRepository.save(escortShip);
    }

    private void giveBackAsset(EscortShip escortShip, Assets assets) {
        var amount = escortShip.getNumberOfShipBuilding();
        assets.setGold(assets.getGold() + escortShip.getEscortShipConfig().getGold() * amount * escortShip.getPercentRssBuild());
        assets.setStone(assets.getStone() + escortShip.getEscortShipConfig().getStone() * amount * escortShip.getPercentRssBuild());
        assets.setWood(assets.getWood() + escortShip.getEscortShipConfig().getWood() * amount * escortShip.getPercentRssBuild());
        assetsService.save(assets);
    }

    public List<EscortShip> saveAll(List<EscortShip> escortShips) {
        return escortShipRepository.saveAll(escortShips);
    }

    private EscortShip updateAmount(EscortShip escortShip, Long nChange) {
        escortShip.setAmount(escortShip.getAmount() + nChange);
        escortShipRepository.save(escortShip);
        return escortShip;
    }

    public EscortShip increaseAmount(EscortShip escortShip, Long nChange) {
        if (nChange < 0) {throw KOSException.of(ErrorCode.SERVER_ERROR);}
        return updateAmount(escortShip, nChange);
    }

    public EscortShip decreaseAmount(EscortShip escortShip, Long nChange) {
        if (nChange < 0 || nChange > escortShip.getAmount()) {throw KOSException.of(ErrorCode.SERVER_ERROR);}
        return updateAmount(escortShip, -nChange);

    }

    public void takeEscortShipWhenBattle(List<EscortShip> escortShips) {
        for (EscortShip escortShip : escortShips) {
            escortShip.setAmount(0L);
        }
        escortShipRepository.saveAll(escortShips);
    }
}
