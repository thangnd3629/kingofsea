package com.supergroup.kos.upgrading.consumer;

import static com.supergroup.core.constant.MessageBrokerConstants.MOVE_SESSION_DLQ;
import static com.supergroup.core.constant.MessageBrokerConstants.MOVE_SESSION_EXCHANGE;
import static com.supergroup.core.constant.MessageBrokerConstants.MOVE_SESSION_STATUS_CHANGE_DLQ;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supergroup.core.constant.MessageBrokerConstants;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.SeaActivityAsyncTask;
import com.supergroup.kos.building.domain.command.GetElementByCoordinatesCommand;
import com.supergroup.kos.building.domain.command.WithdrawActivityCommand;
import com.supergroup.kos.building.domain.dto.seamap.MoveSessionDTO;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.ShipElement;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;
import com.supergroup.kos.building.domain.service.seamap.MapService;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;
import com.supergroup.kos.building.domain.service.seamap.activity.MoveSessionService;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;
import com.supergroup.kos.building.domain.service.seamap.activity.arrivalHandler.MoveSessionHandlerFactory;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MoveSessionDelayQueueConfig {
    private final ObjectMapper              SERIALIZER;
    private final MoveSessionHandlerFactory handlerFactory;
    private final SeaActivityService        seaActivityService;
    private final MoveSessionService        moveSessionService;
    private final SeaElementService         seaElementService;
    private final SeaActivityAsyncTask      seaActivityAsyncTask;
    private final MapService                mapService;

    @Bean(value = MOVE_SESSION_EXCHANGE)
    CustomExchange moveSessionExchange() {
        var args = new HashMap<String, Object>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(MOVE_SESSION_EXCHANGE, "x-delayed-message", true, false, args);
    }

    @Bean(value = MessageBrokerConstants.MOVE_SESSION_QUEUE)
    public Queue moveSessionQueue() {
        return QueueBuilder.durable(MessageBrokerConstants.MOVE_SESSION_QUEUE)
                           .withArgument("x-dead-letter-exchange", "")
                           .withArgument("x-dead-letter-routing-key", MOVE_SESSION_DLQ)
                           .build();
    }
    @Bean
    public Queue moveSessionDLQ() {
        return QueueBuilder.durable(MOVE_SESSION_DLQ)
                           .build();
    }

    @Bean
    Binding bindingMoveSessionQueue(@Qualifier(value = MessageBrokerConstants.MOVE_SESSION_QUEUE) Queue queue,
                                    @Qualifier(value = MessageBrokerConstants.MOVE_SESSION_EXCHANGE) CustomExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(MessageBrokerConstants.MOVE_SESSION_QUEUE)
                .noargs();
    }

    @RabbitListener(queues = MessageBrokerConstants.MOVE_SESSION_QUEUE)
    @Transactional
    public void processMoveSession(String input) throws Exception {
        try {
            MoveSessionDTO message = SERIALIZER.readValue(input, MoveSessionDTO.class);
            log.info("Process move session {}", message.getId());
            SeaActivity activity = seaActivityService.findById(message.getSeaActivityId());
            Optional<MoveSession> optionalMove = moveSessionService.getById(message.getId());
            if (optionalMove.isEmpty()) {
                log.info("Waiting for entity to be flushed. Consume message one more time");
//                throw KOSException.of(ErrorCode.CONSUME_MESSAGE_BEFORE_FLUSHING_ENTITY);
                return;
            }
            MoveSession moveSession = optionalMove.get();
            if (moveSession.getIsDeleted().equals(Boolean.TRUE) || moveSession.getIsProcessed().equals(Boolean.TRUE)) {
                log.info("Session was invalidated");
                return;
            }
            if (Objects.nonNull(moveSession.getWithdrawnTime())) {
                return;
            }
            // check coordinate
            Coordinates destinationOnMap = activity.getActiveMoveSession().getEnd();
            var cachedElement = mapService.getElementByCoordinates(new GetElementByCoordinatesCommand(destinationOnMap))
                                          .stream()
                                          .filter(SeaElement::getActive)
                                          .filter(e -> e.getId().equals(moveSession.getDestinationElementId()))
                                          .filter(element -> !(element instanceof ShipElement))
                                          .findFirst().orElse(null);
            // if element is not found
            // notify user no target found
            // then withdraw
            if (Objects.isNull(cachedElement)) {
                seaActivityAsyncTask.sendTargetNotFoundNotification(moveSession.getSeaActivity().getKosProfile().getUser().getId());
                seaActivityService.withdraw(new WithdrawActivityCommand().setId(activity.getId()));
                return;
            }
            // get element to process
            SeaElement destinationElement = seaElementService.getElementById(cachedElement.getId());
            activity.setCurrentLocation(destinationOnMap);
            seaElementService.deleteById(activity.getShipElement().getId());
            activity.setShipElement(null);
            seaActivityService.save(activity);
            var handler = handlerFactory.getInstance(message.getDestinationType());
            handler.handleMove(destinationElement, moveSession, activity);
            log.info("Process move session {} successfully", message.getId());
            moveSession.setIsProcessed(true);
            moveSessionService.save(moveSession);
        } catch (Exception e) {
            if (e instanceof KOSException) {
                e.printStackTrace();
                throw e;
            } else {
                Sentry.captureException(e);
                throw e;
            }
        }
    }
}
