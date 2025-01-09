package com.supergroup.kos.building.domain.async;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.supergroup.core.constant.NotificationIntents;
import com.supergroup.core.constant.NotificationRenderContentPlaceHolder;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.notification.domain.model.NotificationTemplateType;
import com.supergroup.kos.notification.domain.service.NotificationTemplateService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UpgradeServiceAsyncTask {

    private final NotificationTemplateService notificationTemplateService;

    public void sendUpgradeNotification(Long userId, BuildingName buildingName, Long level) {
        try {
            JSONArray intents = new JSONArray();
            JSONObject navigateToStorage = new JSONObject();
            navigateToStorage.put(NotificationIntents.INTENT_KEY, NotificationIntents.BUILDING_UPGRADE);
            navigateToStorage.put(NotificationIntents.BUILDING, buildingName.getBuildingName());
            intents.put(navigateToStorage);
            Map<String, Object> params = new HashMap<>();
            params.put(NotificationRenderContentPlaceHolder.LEVEL, level);
            params.put(NotificationRenderContentPlaceHolder.BUILDING_NAME, buildingName.getBuildingName());
            notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.BUILDING_UPGRADE_SUCCESS, params, intents.toList(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
