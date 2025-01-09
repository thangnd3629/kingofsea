package com.supergroup.kos.building.domain.service.seamap.activity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.SeaActivityAsyncTask;
import com.supergroup.kos.building.domain.constant.seamap.SeaActivityStatus;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.dto.seamap.CreateMoveSessionEvent;
import com.supergroup.kos.building.domain.mapper.MoveSessionMapper;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.model.seamap.movesession.MineMoveSession;
import com.supergroup.kos.building.domain.model.seamap.movesession.MissionType;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;
import com.supergroup.kos.building.domain.model.seamap.movesession.NpcMoveSession;
import com.supergroup.kos.building.domain.model.seamap.movesession.UserBaseMoveSession;
import com.supergroup.kos.building.domain.repository.persistence.seamap.MoveSessionRepository;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;
import com.supergroup.kos.building.domain.utils.DistanceUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MoveSessionService {
    public static final String MOVE_SESSION_EXCHANGE = "MOVE_SESSION_EXCHANGE";
    public static final String MOVE_SESSION_QUEUE    = "MOVE_SESSION_QUEUE";

    private final MoveSessionRepository      repository;
    private final RabbitTemplate             rabbitTemplate;
    private final MoveSessionMapper          moveSessionMapper;
    private final ShipMovingAnimationService shipMovingAnimationService;
    private final ObjectMapper               objectMapper;
    private final ApplicationEventPublisher  publisher;
    private final SeaElementService          seaElementService;
    private final SeaActivityAsyncTask       seaActivityAsyncTask;
    private final LineUpService              lineUpService;

    @Transactional
    public MoveSession attachMoveSession(SeaActivity activity, MoveSession moveSession) {
        try {
            activity.setActiveMoveSession(moveSession).setCurrentLocation(null);
            SeaActivityStatus missionStatus = SeaActivityStatus.MOVING;
            activity.setStatus(missionStatus);
            ShipLineUp lineUp = activity.getLineUp();
            if (Objects.nonNull(lineUp)) {
                lineUp.getMotherShip().setStatus(missionStatus);
                moveSession.setShipUnits(lineUp.getEscortShipUnits());
            }
            double travellingDuration = DistanceUtils.getDistance(moveSession.getStart(), moveSession.getEnd()) / activity.getSpeed();
            moveSession.setSeaActivity(activity)
                       .setSpeed(activity.getSpeed())
                       .setDuration(travellingDuration);
            var savedMoveSession = repository.save(moveSession);
            shipMovingAnimationService.updateShipMoveAnimation(activity, savedMoveSession);
            if (Objects.isNull(savedMoveSession.getId())) {
                throw new IllegalArgumentException("Move session id must not be nul");
            }

            SeaElement source = null;
            if (Objects.nonNull(moveSession.getSourceElementId())) {
                source = seaElementService.findElementById(moveSession.getSourceElementId());
            }
            SeaElement destination = seaElementService.findElementById(moveSession.getDestinationElementId());
            seaActivityAsyncTask.sendMoveSessionStatusChange(source, destination, activity);
            log.info("Send move session {}", savedMoveSession.getId());
            publisher.publishEvent(new CreateMoveSessionEvent(savedMoveSession));
            return savedMoveSession;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        }
    }

    @Transactional
    public MoveSession buildMoveSession(Coordinates source,
                                        Long sourceElementId,
                                        Coordinates destination,
                                        Long destinationElementId,
                                        SeaElementType destinationType,
                                        MissionType missionType
                                       ) {
        MoveSession moveSession;
        switch (destinationType) {
            case USER_BASE:
                moveSession = new UserBaseMoveSession();
                break;
            case BOSS:
                moveSession = new NpcMoveSession();
                break;
            case RESOURCE:
                moveSession = new MineMoveSession();
                break;
            default:
                throw KOSException.of(ErrorCode.TROOP_MOVEMENT_TYPE_NOT_SPECIFIED);
        }

        moveSession.setStart(source)
                   .setEnd(destination)
                   .setTimeStart(LocalDateTime.now())
                   .setDestinationElementId(destinationElementId)
                   .setMissionType(missionType)
                   .setSourceElementId(sourceElementId);
        return repository.save(moveSession);
    }

    public MoveSession save(MoveSession moveSession) {
        repository.save(moveSession);
        return moveSession;
    }

    public void sendToQueue(MoveSession moveSession) {
        try {
            var taskJson = objectMapper.writeValueAsString(moveSessionMapper.toDTO(moveSession)
                                                                            .setUuid(String.valueOf(UUID.randomUUID())));
            var prop = new MessageProperties();
            prop.setHeader("x-delay", moveSession.getDuration() * 1000); // milisec
            var mess = MessageBuilder.withBody(taskJson.getBytes())
                                     .andProperties(prop)
                                     .build();
            rabbitTemplate.convertAndSend(MOVE_SESSION_EXCHANGE, MOVE_SESSION_QUEUE, mess);
        } catch (JsonProcessingException e) {
            log.error("Error sending session to queue");
            throw new KOSException(ErrorCode.SERVER_ERROR);
        }

    }

    public Page<MoveSession> getTroopMovement(Long kosProfileId, Pageable pageable) {
        return repository.getAllTroopMovement(kosProfileId, pageable);
    }

    public MoveSession deleteMoveSession(Long kosProfileId, Long moveSessionId) {
        MoveSession moveSession = repository.getBySeaActivityKosProfileIdAndId(kosProfileId, moveSessionId).orElseThrow(
                () -> KOSException.of(ErrorCode.TROOP_MOVEMENT_HISTORY_NOT_FOUND));
        moveSession.setIsHidden(true);
        repository.save(moveSession);
        return moveSession;
    }

    Boolean existsInProgressMoveWithMissionToTarget(MissionType missionType, Long destinationElementId, Long kosProfileId) {
        if (missionType.equals(MissionType.RETURN)) {return false;}
        return repository.existsInProgressMoveWithMissionToTarget(missionType, destinationElementId, kosProfileId) > 0;
    }

    public Optional<MoveSession> getById(Long id) {
        return repository.findById(id);
    }
}
