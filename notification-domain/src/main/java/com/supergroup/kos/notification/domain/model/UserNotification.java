package com.supergroup.kos.notification.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.supergroup.auth.domain.model.User;
import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.notification.domain.constant.NotificationStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_user_notification")
@Accessors(chain = true)
@Getter
@Setter
public class UserNotification extends BaseModel {
    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    /**
     * Owner this notification
     * */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Notification
     * */
    @ManyToOne(fetch = javax.persistence.FetchType.LAZY, cascade = javax.persistence.CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    /**
     * Status of notification can be:
     * - READ: user read
     * - UNREAD: new notification
     * - DELETE: hide this notification
     * */
    private NotificationStatus status = NotificationStatus.UNSEEN;
}
