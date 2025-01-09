package com.supergroup.admin.api;

import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.admin.dto.AdminSendNotificationRequest;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.notification.domain.dto.NotificationDTO;
import com.supergroup.kos.notification.domain.model.Notification;
import com.supergroup.kos.notification.domain.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/notification")
@RequiredArgsConstructor
public class NotificationRestController {

    private final NotificationService notificationService;
    private final KosProfileService kosProfileService;

    @PostMapping()
    public ResponseEntity<?> sendToUser(@RequestBody AdminSendNotificationRequest request) {
        KosProfile kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(request.getUserId()));
        Notification persistentPayload = null;
        NotificationDTO requestPersistentPayload = request.getPersistentPayload();


        if (Objects.nonNull(requestPersistentPayload)) {

            try {
                persistentPayload = new Notification().setBody(requestPersistentPayload.getBody())
                                                      .setTitle(requestPersistentPayload.getTitle())
                                                      .setRenderContents(new JSONArray(requestPersistentPayload.getRewards()));
            } catch (JSONException e) {
                e.printStackTrace();
                throw KOSException.of(ErrorCode.BAD_REQUEST_ERROR);
            }
        }
        notificationService.sendDirect(request.getUserId(), request.getNotificationMessagePayload(), request.getDataMessagePayload(), persistentPayload);
        return ResponseEntity.noContent().build();
    }
}
