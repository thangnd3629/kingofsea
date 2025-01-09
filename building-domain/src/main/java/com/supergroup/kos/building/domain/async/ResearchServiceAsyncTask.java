package com.supergroup.kos.building.domain.async;

import static com.supergroup.core.constant.NotificationRenderContentPlaceHolder.TECH_NAME;

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

public class ResearchServiceAsyncTask {
    private final NotificationTemplateService notificationTemplateService;

    public void sendNotification(String technologyName, Long userId) {
        try {
            Map<String, Object> namedParams = new HashMap<>();
            List<Object> intents = new ArrayList<>();
            namedParams.put(TECH_NAME, technologyName);
            notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.RESEARCH_TECHNOLOGY_SUCCESS, namedParams, intents, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
