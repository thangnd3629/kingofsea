package com.supergroup.kos.notification.domain.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.JSONArray;

import com.supergroup.core.converter.JsonArrayConverter;
import com.supergroup.core.model.BaseModel;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_notification")
@Accessors(chain = true)
@Getter
@Setter
public class Notification extends BaseModel {
    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    /**
     * Title of notification: Ex: Upgrading,...
     */
    private String title;

    @Column(columnDefinition = "text")
    private String    body;
    /**
     * data that contain icon, figures , that can't be rendered using text
     */
    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonArrayConverter.class)
    private JSONArray renderContents;
    private String    actions;

}
