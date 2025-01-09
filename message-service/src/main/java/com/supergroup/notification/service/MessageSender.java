package com.supergroup.notification.service;

import com.google.firebase.messaging.FirebaseMessagingException;

public interface MessageSender {
    void send(FcmMessage fcmMessage) throws FirebaseMessagingException;
}
