package com.supergroup.kos.building.domain.async;

import static com.supergroup.core.constant.NotificationActionPlaceHolder.SCOUT_REPORT_ID;
import static com.supergroup.core.constant.NotificationRenderContentPlaceHolder.POSITION_X;
import static com.supergroup.core.constant.NotificationRenderContentPlaceHolder.POSITION_Y;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.kos.building.domain.constant.MissionResult;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.scout.ScoutReport;
import com.supergroup.kos.notification.domain.model.NotificationTemplateType;
import com.supergroup.kos.notification.domain.service.NotificationTemplateService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ScoutServiceAsyncTask {

    private final NotificationTemplateService notificationTemplateService;

    public void sendScoutNotification(List<ScoutReport> scoutReports) {
        for(ScoutReport scoutReport : scoutReports) {
            try {
                Map<String, Object> namedParams = new HashMap<>();
                List<Object> intents = new ArrayList<>();
                namedParams.put(POSITION_X, scoutReport.getInfoElementTarget().getCoordinates().getX());
                namedParams.put(POSITION_Y, scoutReport.getInfoElementTarget().getCoordinates().getY());
                namedParams.put(SCOUT_REPORT_ID, scoutReport.getId());
                NotificationTemplateType templateType;
                if (scoutReport.getResult().equals(MissionResult.SUCCESS)) {
                    templateType = NotificationTemplateType.SCOUT_MISSION_SUCCESS;
                } else {
                    templateType = NotificationTemplateType.SCOUT_MISSION_FAIL;
                }
                notificationTemplateService.sendByTemplate(scoutReport.getKosProfile().getUser().getId(),
                                                           templateType,
                                                           namedParams,
                                                           intents,
                                                           null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Transactional
    public void sendBetrayedScoutNotification(List<ScoutReport> scoutReports) {
        for(ScoutReport scoutReport : scoutReports) {
            try {
                notificationTemplateService.sendByTemplate(scoutReport.getKosProfile().getUser().getId(),
                                                           NotificationTemplateType.COUNTER_SCOUT_SUCCESS,
                                                           null,
                                                           null,
                                                           null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Transactional
    public void sendNoTargetFoundNotification(KosProfile kosProfile) {
        try {
            notificationTemplateService.sendByTemplate(kosProfile.getUser().getId(), NotificationTemplateType.NO_TARGET_FOUND, new HashMap<>(),
                                                       new ArrayList<>(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
