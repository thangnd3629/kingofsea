package com.supergroup.kos.notification.domain.service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.notification.domain.constant.NotificationStatus;
import com.supergroup.kos.notification.domain.model.UserNotification;
import com.supergroup.kos.notification.domain.repository.UserNotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserNotificationService {
    private final UserNotificationRepository userNotificationRepository;

    public Page<UserNotification> getNotifications(Long userId, List<String> statuses, List<String> types, Pageable pageable) {
        if (Objects.nonNull(statuses) && !statuses.isEmpty() && Objects.nonNull(types) && !types.isEmpty()) {
            return userNotificationRepository.find(
                    userId,
                    statuses.stream().map(NotificationStatus::valueOf).collect(Collectors.toCollection(TreeSet::new)),
                    pageable);
        } else if (Objects.nonNull(statuses) && !statuses.isEmpty()) {
            return userNotificationRepository.find(
                    userId,
                    statuses.stream().map(NotificationStatus::valueOf).collect(Collectors.toCollection(TreeSet::new)),
                    pageable);
        } else if (Objects.nonNull(types) && !types.isEmpty()) {
            return userNotificationRepository.find(
                    userId,
                    Arrays.stream(NotificationStatus.values()).collect(Collectors.toCollection(TreeSet::new)),
                    pageable);
        }
        return userNotificationRepository.findByUserId(userId, pageable);
    }

    @Transactional
    public void updateStatusNotification(Long userNotificationId, Long userId, NotificationStatus status) {
        UserNotification userNotification = userNotificationRepository.findById(userNotificationId).orElseThrow(
                () -> KOSException.of(ErrorCode.BAD_REQUEST_ERROR));
        if (userNotification.getStatus().equals(NotificationStatus.DELETE)) {
            throw KOSException.of(ErrorCode.BAD_REQUEST_ERROR);
        }
        if (!userNotification.getUser().getId().equals(userId)) {
            throw KOSException.of(ErrorCode.BAD_REQUEST_ERROR);
        }
        userNotification.setStatus(status);
        userNotificationRepository.save(userNotification);
    }

    @Transactional
    public void updateStatusAllNotification(Long userId, NotificationStatus status) {
        List<UserNotification> userNotifications = userNotificationRepository.find(
                userId,
                List.of(NotificationStatus.UNSEEN, NotificationStatus.SEEN));
        userNotifications.forEach(userNotification -> {
            userNotification.setStatus(status);
            userNotificationRepository.save(userNotification);
        });
    }

    public Long countNotificationUnseen(Long userId) {
        return userNotificationRepository.countByUser_IdAndStatus(userId, NotificationStatus.UNSEEN);
    }
}
