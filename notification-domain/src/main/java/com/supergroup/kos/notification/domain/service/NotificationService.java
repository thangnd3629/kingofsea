package com.supergroup.kos.notification.domain.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.supergroup.auth.domain.model.User;
import com.supergroup.auth.domain.service.LoginSessionService;
import com.supergroup.auth.domain.service.UserService;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.notification.domain.constant.NotificationStatus;
import com.supergroup.kos.notification.domain.constant.SourceType;
import com.supergroup.kos.notification.domain.model.Notification;
import com.supergroup.kos.notification.domain.model.NotificationProducerPayload;
import com.supergroup.kos.notification.domain.model.UserNotification;
import com.supergroup.kos.notification.domain.repository.UserNotificationRepository;
import com.supergroup.notification.service.DataMessage;
import com.supergroup.notification.service.NotificationMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private static final Long MAX_LENGTH_TITLE = 400L;

    private final UserNotificationRepository userNotificationRepository;
    private final LoginSessionService        loginSessionService;
    private final NotificationProducer       notificationProducer;
    private final UserService                userService;

    /**
     * Send to one user
     *
     * @param userId: User id you want to send
     * @param notificationMessage: Notification in system trace
     * @param dataMessage: Banner in game
     * @param persistentMessage: Notification in game
     */
    @Transactional
    public void sendDirect(Long userId, NotificationMessage notificationMessage, DataMessage dataMessage, Notification persistentMessage) {
        var session = loginSessionService.findFirstByUser_IdOrderByUpdatedAtDesc(userId).orElse(null);
        if (Objects.isNull(session)) {
            return;
        }
        validateDataMessage(dataMessage);
        validateNotificationMessage(notificationMessage);
        if (Objects.nonNull(persistentMessage)) {
            isValidPersistentMessage(persistentMessage);
            saveUserNotification(persistentMessage, userService.getById(userId));
        }
        NotificationProducerPayload payload = new NotificationProducerPayload();
        if (Objects.nonNull(persistentMessage)) {
            if (Objects.isNull(dataMessage)) {
                dataMessage = new DataMessage();
            }
            dataMessage.setIsPersistent(true);
        }
        payload.setDataMessage(dataMessage)
               .setNotificationMessage(notificationMessage)
               .setUserIds(List.of(userId));
        try {
            notificationProducer.sendToQueue(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new KOSException(ErrorCode.SERVER_ERROR);
        }
    }

    public void sendMulti(List<Long> userIds, NotificationMessage notificationMessage, DataMessage dataMessage, Notification persistentMessage,
                          SourceType type) {
        //todo: implement broadcast fcm
    }

    private UserNotification saveUserNotification(Notification notification, User user) {
        var userNotification = new UserNotification();
        userNotification.setUser(user);
        userNotification.setNotification(notification);
        userNotification.setStatus(NotificationStatus.UNSEEN);
        userNotificationRepository.save(userNotification);
        return userNotification;
    }

    private void validateNotificationMessage(NotificationMessage notificationMessage) {
        if (Objects.isNull(notificationMessage)) {
            return;
        }
        if (Objects.nonNull(notificationMessage.getTitle()) && notificationMessage.getTitle().length() > MAX_LENGTH_TITLE) {
            throw KOSException.of(ErrorCode.TITLE_IS_TOO_LONG);
        }

    }

    private void isValidPersistentMessage(Notification notification) {
        if (Objects.isNull(notification)) {
            return;
        }
        if (Objects.nonNull(notification.getTitle()) && notification.getTitle().length() > MAX_LENGTH_TITLE) {
            throw KOSException.of(ErrorCode.TITLE_IS_TOO_LONG);
        }

    }

    private void validateDataMessage(DataMessage dataMessage) {

    }

}
