package com.supergroup.kos.building.domain.async;

import static com.supergroup.core.constant.NotificationRenderContentPlaceHolder.COMBAT_TYPE;
import static com.supergroup.core.constant.NotificationRenderContentPlaceHolder.POSITION_X;
import static com.supergroup.core.constant.NotificationRenderContentPlaceHolder.POSITION_Y;
import static com.supergroup.core.constant.NotificationRenderContentPlaceHolder.ROUND_NO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.auth.domain.model.User;
import com.supergroup.core.constant.NotificationIntents;
import com.supergroup.kos.building.domain.model.battle.BattleReport;
import com.supergroup.kos.building.domain.model.battle.RoundReport;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;
import com.supergroup.kos.notification.domain.model.NotificationTemplateType;
import com.supergroup.kos.notification.domain.service.NotificationTemplateService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BattleReportServiceAsyncTask {

    private final NotificationTemplateService notificationTemplateService;
    private final UserBaseService             userBaseService;

    @Transactional
    public void sendBattleReportNotification(BattleReport battleReport, List<User> joiners) {
        try {
            JSONArray intents = new JSONArray();
            JSONObject navigateToCombatReport = new JSONObject();
            navigateToCombatReport.put(NotificationIntents.INTENT_KEY, NotificationIntents.NAVIGATE_COMBAT_REPORT);
            navigateToCombatReport.put(NotificationIntents.ID, battleReport.getId());
            JSONObject battleEndIntents = new JSONObject();
            battleEndIntents.put(NotificationIntents.INTENT_KEY, NotificationIntents.BATTLE_END);
            intents.putAll(List.of(navigateToCombatReport, battleEndIntents));
            Map<String, Object> namedParams = new HashMap<>();
            namedParams.put(COMBAT_TYPE, battleReport.getBattle().getBattleType().getDisplayName());
            for (User joiner : joiners) {
                notificationTemplateService.sendByTemplate(joiner.getId(),
                                                           NotificationTemplateType.ATTACK_COMBAT_END,
                                                           namedParams, intents.toList(),
                                                           null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void sendRoundReportNotification(RoundReport roundReport, Set<Long> userIds) {
        try {
            JSONArray intents = new JSONArray();
            JSONObject navigateToBattleField = new JSONObject();
            navigateToBattleField.put(NotificationIntents.INTENT_KEY, NotificationIntents.JUMP_TO_POSITION);
            navigateToBattleField.put(NotificationIntents.POSITION_X, roundReport.getBattleReport().getBattle().getBattleField().getX());
            navigateToBattleField.put(NotificationIntents.POSITION_Y, roundReport.getBattleReport().getBattle().getBattleField().getY());
            intents.put(navigateToBattleField);
            Map<String, Object> namedParams = new HashMap<>();
            namedParams.put(COMBAT_TYPE, roundReport.getBattleReport().getBattle().getBattleType());
            namedParams.put(ROUND_NO, ordinal(roundReport.getRound().getIndex().intValue()));
            for (Long userId : userIds) {
                notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.BATTLE_ROUND_ENDED, namedParams, intents.toList(),
                                                           null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUserJoinForceNotification(Long userId) {
        try {
            List<Object> intents = new ArrayList<>();
            Map<String, Object> namedParams = new HashMap<>();
            notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.TROOP_JOIN_BATTLE, namedParams, intents, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendAllyForceNotification(Long userId, SeaActivity activity) {
        try {
            UserBase userBase = userBaseService.getByKosProfileId(activity.getKosProfile().getId());
            List<Object> intents = new ArrayList<>();
            Map<String, Object> namedParams = new HashMap<>();
            namedParams.put(POSITION_X, userBase.getX());
            namedParams.put(POSITION_Y, userBase.getY());
            notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.ALLIANCE_JOIN_BATTLE, namedParams, intents, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String ordinal(int i) {
        String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + suffixes[i % 10];

        }
    }
}
