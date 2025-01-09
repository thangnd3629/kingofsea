package com.supergroup.kos.building.domain.async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.auth.domain.model.User;
import com.supergroup.core.constant.NotificationIntents;
import com.supergroup.kos.notification.domain.model.NotificationTemplateType;
import com.supergroup.kos.notification.domain.service.NotificationTemplateService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class BattleWithdrawalAsyncTask {

    private final NotificationTemplateService notificationTemplateService;

    @Async
    @Transactional
    public void sendWithdrawalNotification(User defender) {
        JSONArray intents = new JSONArray();
        JSONObject disableWarningScreen = new JSONObject();
        disableWarningScreen.put(NotificationIntents.INTENT_KEY, NotificationIntents.BATTLE_CANCEL);
        intents.put(disableWarningScreen);
        notificationTemplateService.sendByTemplate(defender.getId(), NotificationTemplateType.CANCEL_COMBAT, new HashMap<>(), intents.toList(), null);
    }

    public void sendWithdrawNextRoundNotification(User attacker) {
        Map<String, Object> namedParams = new HashMap<>();
        List<Object> intents = new ArrayList<>();
        notificationTemplateService.sendByTemplate(attacker.getId(), NotificationTemplateType.WITHDRAW_IN_COMBAT, namedParams, intents, null);
    }
}
