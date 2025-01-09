package com.supergroup.kos.building.domain.async;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.supergroup.core.constant.NotificationIntents;
import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.notification.domain.model.NotificationTemplateType;
import com.supergroup.kos.notification.domain.service.NotificationTemplateService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MotherShipServiceAsyncTask {

    private final NotificationTemplateService notificationTemplateService;

    public void sendHealingNotification(MotherShip motherShip) {
        // send message to client: notify healing
        var intents = new JSONArray();
        var healingNotifyIntent = new JSONObject();
        healingNotifyIntent.put(NotificationIntents.INTENT_KEY, NotificationIntents.MOTHER_SHIP_HEAL_STATUS_CHANGED);
        healingNotifyIntent.put("isHealing", motherShip.isHealing());
        healingNotifyIntent.put("id", motherShip.getId());
        intents.put(healingNotifyIntent);
        notificationTemplateService.sendByTemplate(motherShip.getCommandBuilding().getKosProfile().getUser().getId(),
                                                   NotificationTemplateType.MOTHER_SHIP_HEAL_STATUS_CHANGED,
                                                   null,
                                                   intents.toList(),
                                                   null);
    }

}
