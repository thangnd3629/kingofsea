package com.supergroup.kos.notification.domain.repository;

import java.util.Optional;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.notification.domain.model.NotificationTemplate;
import com.supergroup.kos.notification.domain.model.NotificationTemplateType;

public interface NotificationTemplateRepository extends BaseJpaRepository<NotificationTemplate> {
    Optional<NotificationTemplate> findByTemplateType(NotificationTemplateType templateType);
}
