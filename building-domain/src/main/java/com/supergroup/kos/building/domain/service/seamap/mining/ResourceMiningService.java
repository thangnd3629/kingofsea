package com.supergroup.kos.building.domain.service.seamap.mining;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.SeaActivityAsyncTask;
import com.supergroup.kos.building.domain.command.SaveOrUpdateElementCommand;
import com.supergroup.kos.building.domain.constant.WeaponStat;
import com.supergroup.kos.building.domain.constant.seamap.ResourceIslandType;
import com.supergroup.kos.building.domain.constant.seamap.SeaActivityStatus;
import com.supergroup.kos.building.domain.dto.seamap.SeaMiningSessionDTO;
import com.supergroup.kos.building.domain.model.config.seamap.ResourceIslandConfig;
import com.supergroup.kos.building.domain.model.seamap.ResourceIsland;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaMiningSession;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.model.seamap.reward.LoadedOnShipReward;
import com.supergroup.kos.building.domain.model.seamap.reward.MiningReward;
import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.building.domain.repository.persistence.seamap.ResourceMiningSessionRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementConfigRepository;
import com.supergroup.kos.building.domain.service.seamap.KosWarInfoService;
import com.supergroup.kos.building.domain.service.seamap.MapService;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResourceMiningService {
    private final       ResourceMiningSessionRepository                  repository;
    private final       RabbitTemplate                                   rabbitTemplate;
    private final       ObjectMapper                                     SERIALIZER;
    private final       MapService                                       mapService;
    private final       SeaElementService                                seaElementService;
    private final       SeaElementConfigRepository<ResourceIslandConfig> resourceConfigRepo;
    private final       KosWarInfoService                                kosWarInfoService;
    private final       SeaActivityAsyncTask                             seaActivityAsyncTask;
    public static final String                                           MINING_SESSION_EXCHANGE = "MINING_SESSION_EXCHANGE";
    public static final String                                           MINING_SESSION_QUEUE    = "MINING_SESSION_QUEUE";
    public static final String                                           MINING_SESSION_DLQ      = "MINING_SESSION_DLQ";

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void test() {
        repository.save(new SeaMiningSession().setResourceType(ResourceIslandType.STONE));
    }

    @Transactional
    public SeaMiningSession startMiningSession(SeaActivity activity, ResourceIsland element) {
        ResourceIslandConfig config = resourceConfigRepo.findResourceIslandConfigById(element.getSeaElementConfig().getId()).orElseThrow(
                () -> KOSException.of(ErrorCode.SERVER_ERROR));
        ShipLineUp lineUp = activity.getLineUp();
        MotherShip motherShip = lineUp.getMotherShip();
        long tonnageStatWeapon = kosWarInfoService.getWeaponPower(motherShip.getWeapons(), WeaponStat.TNG).longValue();
        long tonnageStatWeaponSet = kosWarInfoService.getWeaponSetPower(motherShip.getWeaponSets(), WeaponStat.TNG).longValue();
        double tonnageStat = (motherShip.getMotherShipConfigQualityConfig().getMotherShipConfig().getTng() *
                              motherShip.getMotherShipConfigQualityConfig().getMotherShipQualityConfig().getPercentStat() *
                              motherShip.getMotherShipLevelConfig().getPercentStat())
                             + tonnageStatWeapon + tonnageStatWeaponSet;
        LoadedOnShipReward loadedOnShipReward = activity.getLoadedOnShipReward();
        Double shipRemainingCap = tonnageStat;
        switch (((ResourceIslandConfig) element.getSeaElementConfig()).getResourceType()) {
            case STONE:
                shipRemainingCap -= loadedOnShipReward.getStone();
                break;
            case WOOD:
                shipRemainingCap -= loadedOnShipReward.getWood();
                break;
        }

        Double collectedResource = 0D;
        if (Objects.nonNull(element.getMiningSession())) {
            if (activity.getKosProfile().getId().equals(element.getMiningSession().getSeaActivity().getKosProfile().getId())) {
                collectedResource = element.getMiningSession().getCollectedResource();
            }
        }

        SeaMiningSession session = buildSession(activity, element, LocalDateTime.now(), config.getResourceExploitSpeed(),
                                                collectedResource, shipRemainingCap,
                                                config.getResourceCapacity(), config.getResourceType());
        element.setMiningSession(session);
        session.setResourceIsland(element);
        activity.setTimeEnd(session.getTimeStart().plus((long) (session.getDuration() * 1000), ChronoUnit.MILLIS));
        activity.getLineUp().getMotherShip().setStatus(SeaActivityStatus.MINING);
        activity.setStatus(SeaActivityStatus.MINING);
        repository.saveAndFlush(session);
        mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(element));
        sendToQueue(session,
                    session.getDuration().longValue());
        return session;
    }

    private MiningReward getCollectedResource(SeaMiningSession session, LocalDateTime now) {
        ResourceIsland resourceIsland = session.getResourceIsland();
        ResourceIslandConfig config = resourceConfigRepo.findResourceIslandConfigById(resourceIsland.getSeaElementConfig().getId()).orElseThrow(
                () -> KOSException.of(ErrorCode.SERVER_ERROR));
        double elapsedTime = Math.ceil(ChronoUnit.MILLIS.between(session.getTimeStart(), now) / 1000D);
        if (elapsedTime > session.getDuration()) {
            elapsedTime = session.getDuration();
        }
        double collectedResource = (session.getCollectedResource() + session.getSpeed() * elapsedTime);
        MiningReward miningReward = new MiningReward();
        switch (config.getResourceType()) {
            case WOOD:
                miningReward.setWood(collectedResource);
                break;
            case STONE:
                miningReward.setStone(collectedResource);
                break;
        }
        return miningReward;
    }

    public SeaMiningSession getById(Long id) {
        return repository.findById(id).orElseThrow(() -> KOSException.of(ErrorCode.SEA_MINING_SESSION_NOT_FOUND));
    }

    public Optional<SeaMiningSession> findById(Long id) {
        return repository.findById(id);
    }

    private Double getMiningTime(Double collectedResource, Double shipRemainingCap, Double remainingMineRss, Double speed) {
        Double maxCollectableRss;
        if (remainingMineRss > shipRemainingCap) {
            maxCollectableRss = shipRemainingCap;
        } else {
            maxCollectableRss = remainingMineRss;
        }
        if (collectedResource > maxCollectableRss) {
            throw KOSException.of(ErrorCode.SERVER_ERROR);
        }
        return (maxCollectableRss - collectedResource) / speed;
    }

    private SeaMiningSession buildSession(SeaActivity activity, ResourceIsland resourceIsland, LocalDateTime startTime, Double speed,
                                          Double collectedResource, Double shipRemainingCap, Double mineMaxCap, ResourceIslandType type) {
        SeaMiningSession session = new SeaMiningSession();
        session.setSeaActivity(activity);
        session.setTimeStart(startTime);
        session.setCollectedResource(collectedResource);
        session.setTonnage(shipRemainingCap);
        session.setSpeed(speed);
        session.setIsDeleted(false);
        session.setResourceType(type);
        Double expectedMiningDuration = getMiningTime(collectedResource, shipRemainingCap, mineMaxCap - resourceIsland.getMined(), speed);
        session.setDuration(expectedMiningDuration);
        return session;
    }

    @Transactional
    public void pauseSession(SeaMiningSession session) {
        LocalDateTime stoppedAt = LocalDateTime.now();
        MiningReward reward = getCollectedResource(session, stoppedAt);
        session.setIsDeleted(true);
        session.setCollectedResource(reward.getStone() + reward.getWood());
        repository.save(session);
    }

    @Transactional
    public void endSession(SeaMiningSession session, Consumer<MiningReward> miningRewardConsumer) {
        MiningReward reward = new MiningReward();
        if (session.getIsDeleted()) {
            if (session.getResourceType().equals(ResourceIslandType.WOOD)) {
                reward.setWood(session.getCollectedResource());
            } else {
                reward.setStone(session.getCollectedResource());
            }
        } else {
            reward = getCollectedResource(session, LocalDateTime.now());
        }
        ResourceIsland resourceIsland = session.getResourceIsland();
        ResourceIslandConfig config = resourceConfigRepo.findResourceIslandConfigById(resourceIsland.getSeaElementConfig().getId()).orElseThrow(
                () -> KOSException.of(ErrorCode.SERVER_ERROR));
        Double minedRssInSession = 0D;
        switch (session.getResourceType()) {
            case STONE:
                minedRssInSession += reward.getStone();
                break;
            case WOOD:
                minedRssInSession += reward.getWood();
                break;
        }
        session.setIsDeleted(true);
        seaActivityAsyncTask.sendNotificationFinishMiningMission(config.getResourceType(), session.getCollectedResource() + minedRssInSession,
                                                                 session.getSeaActivity().getKosProfile().getUser().getId());
        resourceIsland.setMined(resourceIsland.getMined() + session.getCollectedResource() + minedRssInSession);
        resourceIsland.setMiningSession(null);
        repository.save(session);
        mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(resourceIsland));
        if (resourceIsland.getMined() >= config.getResourceCapacity().longValue()) {
            seaElementService.deleteById(resourceIsland.getId());
        }
        miningRewardConsumer.accept(reward);
    }

    private void sendToQueue(SeaMiningSession session, Long miningDuration) {
        SeaMiningSessionDTO sessionDto = new SeaMiningSessionDTO().setId(session.getId());
        try {
            String message = SERIALIZER.writeValueAsString(sessionDto);
            var prop = new MessageProperties();
            prop.setHeader("x-delay", miningDuration * 1000);
            var mess = MessageBuilder.withBody(message.getBytes())
                                     .andProperties(prop)
                                     .build();
            rabbitTemplate.convertAndSend(MINING_SESSION_EXCHANGE, MINING_SESSION_QUEUE, mess);
        } catch (JsonProcessingException e) {
            throw KOSException.of(ErrorCode.SERVER_ERROR);
        }
    }

}
