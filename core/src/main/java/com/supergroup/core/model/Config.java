package com.supergroup.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.supergroup.core.constant.ConfigKey;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_config")
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Config extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long      id;
    @Enumerated(EnumType.STRING)
    private ConfigKey key;
    @Column(columnDefinition = "TEXT")
    private String    value;

}
