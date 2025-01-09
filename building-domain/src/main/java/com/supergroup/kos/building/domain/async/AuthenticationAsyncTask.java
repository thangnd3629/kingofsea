package com.supergroup.kos.building.domain.async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.supergroup.kos.notification.domain.model.NotificationTemplateType;
import com.supergroup.kos.notification.domain.service.NotificationTemplateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationAsyncTask {
    private final NotificationTemplateService notificationTemplateService;

    public void sendOtherLocationLoginNotification(Long userId) {
        try {
            List<Object> intents = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();
            notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.AUTHENTICATED_IN_OTHER_DEVICE, params, intents, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
