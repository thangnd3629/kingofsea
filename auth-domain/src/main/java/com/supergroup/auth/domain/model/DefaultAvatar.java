package com.supergroup.auth.domain.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.supergroup.core.model.BaseModel;

import lombok.Getter;

@Entity
@Table(name = "tbl_default_avatar")
@Getter
public class DefaultAvatar extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long   id;
    private String assetId;
}
