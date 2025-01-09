package com.supergroup.kos.building.domain.async;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.supergroup.core.constant.NotificationIntents;
import com.supergroup.core.constant.NotificationRenderContentPlaceHolder;
import com.supergroup.kos.building.domain.constant.StorageType;
import com.supergroup.kos.building.domain.dto.battle.VaultProtectedResource;
import com.supergroup.kos.notification.domain.model.NotificationTemplateType;
import com.supergroup.kos.notification.domain.service.NotificationTemplateService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AssetsServiceAsyncTask {

    private final NotificationTemplateService notificationTemplateService;

    public void sendFullCapNotification(StorageType storageType, Long userId) {
        try {
            Map<String, Object> namedParams = new HashMap<>();
            JSONArray intents = new JSONArray();
            JSONObject navigateToStorage = new JSONObject();
            navigateToStorage.put(NotificationIntents.INTENT_KEY, NotificationIntents.UPGRADE_STORAGE_PROMPT);
            navigateToStorage.put("type", storageType.name());
            navigateToStorage.put("text", NotificationIntents.UPGRADE_STORAGE_PROMPT_TEXT);
            intents.put(navigateToStorage);
            namedParams.put("storage_type", storageType.getStorageName());
            notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.STORAGE_FULL_CAPACITY, namedParams, intents.toList(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendVaultProtectionNotification(Long userId, Double protectPercent, VaultProtectedResource protectedResource) {
        DecimalFormat df = new DecimalFormat("#.#");
        try {
            Map<String, Object> namedParams = new HashMap<>();
            namedParams.put(NotificationRenderContentPlaceHolder.GOLD, protectedResource.getGold());
            namedParams.put(NotificationRenderContentPlaceHolder.STONE, protectedResource.getStone());
            namedParams.put(NotificationRenderContentPlaceHolder.WOOD, protectedResource.getWood());
            namedParams.put(NotificationRenderContentPlaceHolder.WEAPON_COUNT, protectedResource.getWeapons());
            namedParams.put(NotificationRenderContentPlaceHolder.QUEEN_COUNT, protectedResource.getQueens());
            namedParams.put(NotificationRenderContentPlaceHolder.RELICS_COUNT, protectedResource.getRelics());
            namedParams.put(NotificationRenderContentPlaceHolder.VAULT_PROTECTION, df.format(protectPercent * 100));
            notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.VAULT_PROTECTION_ACTIVATE, namedParams, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
