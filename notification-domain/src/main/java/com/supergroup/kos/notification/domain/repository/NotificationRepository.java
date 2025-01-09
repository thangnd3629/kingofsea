package com.supergroup.kos.notification.domain.repository;

import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.notification.domain.model.Notification;

@Repository("NotificationRepositoryJpa")
public interface NotificationRepository extends BaseJpaRepository<Notification> {

}
