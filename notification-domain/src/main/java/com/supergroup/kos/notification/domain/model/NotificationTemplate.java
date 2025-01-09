package com.supergroup.kos.notification.domain.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.JSONArray;

import com.supergroup.core.converter.JsonArrayConverter;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_notification_template")
@Accessors(chain = true)
@Getter
@Setter
public class NotificationTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long                     id;
    @Enumerated(EnumType.STRING)
    private NotificationTemplateType templateType;
    private String                   bannerDetail;
    private String                   persistentTitle;
    private String                   persistentDetail;
    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonArrayConverter.class)
    private JSONArray                renderContents;
    private String                   actions;
    private String                   systemTrayTitle;
    private String                   systemTrayDetail;
}
