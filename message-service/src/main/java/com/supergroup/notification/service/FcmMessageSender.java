package com.supergroup.notification.service;

import java.util.Objects;

import org.json.JSONArray;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.AndroidNotification.Priority;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FcmMessageSender implements MessageSender {

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public void send(FcmMessage fcmMessage) throws FirebaseMessagingException {
        // @formatter:off
        if (fcmMessage.getTo() instanceof FcmTo) {
            // send to one user

            var messBuilder = Message.builder()
                                     .setToken(((FcmTo) fcmMessage.getTo()).getToken());
            // check and put data
            if (Objects.nonNull(fcmMessage.getDataMessage())) {
                DataMessage dataMessage = fcmMessage.getDataMessage();
                messBuilder.putData("body", dataMessage.getBody());
                messBuilder.putData("isPersistent", dataMessage.getIsPersistent().toString());

                messBuilder.putData("intents", String.valueOf(new JSONArray(dataMessage.getIntents())));
            }
            // check and set notification ui
            if (Objects.nonNull(fcmMessage.getNotificationMessage())
                && !fcmMessage.getNotificationMessage().isEmpty()) {
                var notification = Notification.builder()
                                               .setTitle(fcmMessage.getNotificationMessage().getTitle())
                                               .setBody(fcmMessage.getNotificationMessage().getBody())
                                               .build();
                messBuilder.setNotification(notification);
                var androidConfig = AndroidConfig.builder()
                                                 .setNotification(AndroidNotification.builder()
                                                                                     .setPriority(Priority.HIGH) // this is priority of android notification
                                                                                     .build())
                                                 .build();
                messBuilder.setAndroidConfig(androidConfig);
            }
            // send message
            firebaseMessaging.send(messBuilder.build());
        } else if (fcmMessage.getTo() instanceof FcmMultiTo) {
            // send to many user
            // TODO implement later
        }
        // @formatter:on
    }
}
