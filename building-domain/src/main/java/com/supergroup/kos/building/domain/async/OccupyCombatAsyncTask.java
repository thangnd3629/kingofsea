package com.supergroup.kos.building.domain.async;

import static com.supergroup.core.constant.NotificationRenderContentPlaceHolder.GOLD;
import static com.supergroup.core.constant.NotificationRenderContentPlaceHolder.STONE;
import static com.supergroup.core.constant.NotificationRenderContentPlaceHolder.WOOD;

import java.util.HashMap;
import java.util.Map;

import com.supergroup.core.constant.NotificationIntents;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.supergroup.kos.building.domain.model.asset.OccupiedBaseTax;
import com.supergroup.kos.notification.domain.model.NotificationTemplateType;
import com.supergroup.kos.notification.domain.service.NotificationTemplateService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Slf4j
public class OccupyCombatAsyncTask {
    private final NotificationTemplateService notificationTemplateService;

    @Async
    public void sendBaseOccupiedWarningNotification(Long userId) {
        JSONArray intents = new JSONArray();
        JSONObject enableOccupiedScreenIntent = new JSONObject();
        enableOccupiedScreenIntent.put(NotificationIntents.INTENT_KEY, NotificationIntents.BASE_OCCUPIED_BY_ENEMY);
        intents.put(enableOccupiedScreenIntent);
        notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.BASE_OCCUPIED_BY_ENEMY,
                null, intents.toList(), null);
    }

    @Async
    public void sendPeaceNotification(Long userId) {
        JSONArray intents = new JSONArray();
        JSONObject disableOccupiedScreenIntent = new JSONObject();
        disableOccupiedScreenIntent.put(NotificationIntents.INTENT_KEY, NotificationIntents.BASE_LIBERATED);
        intents.put(disableOccupiedScreenIntent);
        notificationTemplateService.sendByTemplate(userId,
                NotificationTemplateType.BASE_LIBERATED,
                null,
                intents.toList(),
                null);
    }

    @Async
    public void sendQueryInvaderForceNotification(Long userId) {
        JSONArray intents = new JSONArray();
        JSONObject queryUpdateInvaderForceIntent = new JSONObject();
        queryUpdateInvaderForceIntent.put(NotificationIntents.INTENT_KEY, NotificationIntents.QUERY_INVADER_FORCE);
        intents.put(queryUpdateInvaderForceIntent);
        log.info("Sent to victim");
        notificationTemplateService.sendByTemplate(userId,
                NotificationTemplateType.QUERY_INVADER_FORCE,
                null,
                intents.toList(),
                null);
    }
    @Async
    public void sendOccupiedBaseTaxCharge(Long occupiedUserId, Long invaderId, OccupiedBaseTax occupiedBaseTax){
        Map<String, Object> namedParams = new HashMap<>();
        namedParams.put(GOLD, occupiedBaseTax.getGold());
        namedParams.put(STONE, occupiedBaseTax.getStone());
        namedParams.put(WOOD, occupiedBaseTax.getWood());
        notificationTemplateService.sendByTemplate(invaderId,
                                                   NotificationTemplateType.INVADER_TAX_PAYMENT,
                                                   namedParams,
                                                   null,
                                                   null);
        notificationTemplateService.sendByTemplate(occupiedUserId,
                                                   NotificationTemplateType.OCCUPIED_BASE_TAX_CHARGE,
                                                   namedParams,
                                                   null,
                                                   null);
    }
}
