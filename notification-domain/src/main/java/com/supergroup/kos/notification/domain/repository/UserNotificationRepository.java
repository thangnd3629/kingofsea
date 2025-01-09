package com.supergroup.kos.notification.domain.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.notification.domain.constant.NotificationStatus;
import com.supergroup.kos.notification.domain.model.UserNotification;

@Repository("UserNotificationRepositoryJpa")
public interface UserNotificationRepository extends BaseJpaRepository<UserNotification> {

    @Query("select u from UserNotification u " +
           "where u.user.id = :id and u.status in :statuses " +
           "order by u.createdAt DESC")
    Page<UserNotification> find(Long id,
                                Collection<NotificationStatus> statuses,
                                Pageable pageable);

    @Query("select u from UserNotification u " +
           "where u.user.id = :id and u.status in :statuses " +
           "order by u.createdAt DESC")
    List<UserNotification> find(Long id,
                                Collection<NotificationStatus> statuses);

    @Query("select u from UserNotification u where u.user.id = ?1")
    Page<UserNotification> findByUserId(Long id, Pageable pageable);

    long countByUser_IdAndStatus(Long id, NotificationStatus status);


}
