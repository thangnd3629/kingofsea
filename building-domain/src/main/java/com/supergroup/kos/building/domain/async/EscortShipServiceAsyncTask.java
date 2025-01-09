package com.supergroup.kos.building.domain.async;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.supergroup.core.constant.NotificationRenderContentPlaceHolder;
import com.supergroup.kos.notification.domain.model.NotificationTemplateType;
import com.supergroup.kos.notification.domain.service.NotificationTemplateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EscortShipServiceAsyncTask {

    private final NotificationTemplateService notificationTemplateService;

    public void sendFinishBuildShipNotification(Long userId, String shipName) {
        try {
            Map<String, Object> namedParams = new HashMap<>();
            namedParams.put(NotificationRenderContentPlaceHolder.GUARD_SHIP_NAME, shipName);
            notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.GUARD_SHIP_BUILT, namedParams, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
