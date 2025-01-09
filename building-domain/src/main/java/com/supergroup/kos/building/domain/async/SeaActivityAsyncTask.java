package com.supergroup.kos.building.domain.async;

import static com.supergroup.core.constant.MessageBrokerConstants.MOVE_SESSION_STATUS_CHANGE_EXCHANGE;
import static com.supergroup.core.constant.MessageBrokerConstants.MOVE_SESSION_STATUS_CHANGE_QUEUE;
import static com.supergroup.core.constant.NotificationRenderContentPlaceHolder.POSITION_X;
import static com.supergroup.core.constant.NotificationRenderContentPlaceHolder.POSITION_Y;
import static com.supergroup.core.constant.NotificationRenderContentPlaceHolder.STONE;
import static com.supergroup.core.constant.NotificationRenderContentPlaceHolder.WOOD;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supergroup.auth.domain.model.User;
import com.supergroup.core.constant.NotificationIntents;
import com.supergroup.kos.building.domain.constant.seamap.ResourceIslandType;
import com.supergroup.kos.building.domain.dto.movesession.MoveSessionChangeMessage;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;
import com.supergroup.kos.notification.domain.model.NotificationTemplateType;
import com.supergroup.kos.notification.domain.service.NotificationTemplateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeaActivityAsyncTask {

    private final NotificationTemplateService notificationTemplateService;
    private final RabbitTemplate rabbitTemplate;
    private final UserBaseService userBaseService;
    private final KosProfileService kosProfileService;
    private final ObjectMapper objectMapper;

    /**
     * Send message to notify client move session changes
     */
    public void sendMoveSessionStatusChange(SeaElement source, SeaElement destination, SeaActivity activity) {
        Long sourceId = Objects.nonNull(source) ? source.getId() : null;
        Long destinationId = Objects.nonNull(destination) ? destination.getId() : null;
        var message = new MoveSessionChangeMessage(sourceId, destinationId, activity.getId());
        try {
            var prop = new MessageProperties();
            prop.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            var mess = MessageBuilder.withBody(objectMapper.writeValueAsString(message).getBytes())
                    .andProperties(prop)
                    .build();
            rabbitTemplate.convertAndSend(MOVE_SESSION_STATUS_CHANGE_EXCHANGE, MOVE_SESSION_STATUS_CHANGE_QUEUE, mess);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            // ignore
        }
    }

    public void sendQueryBattleStatusNotification(Long userId) {
        JSONArray intents = new JSONArray();
        JSONObject queryBattleStatus = new JSONObject();
        queryBattleStatus.put(NotificationIntents.INTENT_KEY, NotificationIntents.QUERY_BATTLE_STATUS);
        intents.put(queryBattleStatus);
        try {
            notificationTemplateService.sendByTemplate(userId,
                    NotificationTemplateType.QUERY_BATTLE_STATUS,
                    new HashMap<>(),
                    intents.toList(),
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            // ignore
        }
    }

    @Transactional
    public void sendAttackNotification(Long userId, Coordinates battleSite) {
        try {
            Optional<KosProfile> kosProfile = kosProfileService.findByUserId(userId);
            if (kosProfile.isEmpty()) {
                return;
            }
            JSONArray intents = new JSONArray();
            JSONObject showCombatWarning = new JSONObject();
            showCombatWarning.put(NotificationIntents.INTENT_KEY, NotificationIntents.UPCOMING_COMBAT_WARNING);
            showCombatWarning.put(NotificationIntents.POSITION_X, battleSite.getX());
            showCombatWarning.put(NotificationIntents.POSITION_Y, battleSite.getY());
            intents.put(showCombatWarning);
            notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.BE_ATTACKED, new HashMap<>(), intents.toList(), null);
        } catch (Exception e) {
            e.printStackTrace();
            // ignore
        }
    }

    public void sendReturnBaseNotification(Long userId) {
        try {
            notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.RETURN_BASE_ARRIVAL, new HashMap<>(), new ArrayList<>(),
                    null);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Async
    public void sendTargetNotFoundNotification(Long userId) {
        try {
            notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.NO_TARGET_FOUND, new HashMap<>(), new ArrayList<>(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void sendNotificationFinishMiningMission(ResourceIslandType type, Double reward, Long userId) {
        try {
            Map<String, Object> namedParams = new HashMap<>();
            reward = Math.ceil(reward);
            if (type.equals(ResourceIslandType.WOOD)) {
                namedParams.put(WOOD, reward);
            } else {
                namedParams.put(STONE, reward);
            }
            List<Object> intents = new ArrayList<>();
            notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.MINING_MISSION_SUCCESS, namedParams, intents, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void sendCancelMoveSessionNotification(User user) {
        try {
            Map<String, Object> namedParams = new HashMap<>();
            JSONArray intents = new JSONArray();
            JSONObject cancelBattle = new JSONObject();
            cancelBattle.put(NotificationIntents.INTENT_KEY, NotificationIntents.BATTLE_CANCEL);
            intents.put(cancelBattle);
            notificationTemplateService.sendByTemplate(user.getId(), NotificationTemplateType.WITHDRAW_BEFORE_ARRIVAL, namedParams, intents.toList(),
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
