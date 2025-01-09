package com.supergroup.auth.domain.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.supergroup.core.model.BaseModel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import lombok.experimental.Accessors;

/**
 * Copyright 2021 {@author Loda} (https://loda.me).
 * This project is licensed under the MIT license.
 */
@Entity
@Table(name = "tbl_user_profile")
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserProfile extends BaseModel {
    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String        avatarUrl;
    private String        username; //real name
    private String        fullName;
    private LocalDateTime birthday;

    // using fetch LAZY to boost performance, read more document.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @Exclude // exclude to String
    @JsonBackReference
    private User user;

}
