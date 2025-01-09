package com.supergroup.auth.domain.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.supergroup.auth.domain.constant.UserStatus;
import com.supergroup.auth.domain.constant.UserTag;
import com.supergroup.core.model.BaseModel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Copyright 2021 {@author Loda} (https://loda.me).
 * This project is licensed under the MIT license.
 */
@Entity
@Table(name = "tbl_user", indexes = @Index(columnList = "email, originEmail"))
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class User extends BaseModel implements UserDetails, Verifiable {
    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String     password;
    @Column(nullable = false, unique = true)
    private String     email;
    @Column(nullable = false, unique = true)
    private String     originEmail; // This is normalized email
    private UserStatus userStatus = UserStatus.ACTIVE;
    private UserTag    tag;

    private Boolean       isLocked          = false;
    private LocalDateTime lockTime;
    private Long          loginFailAttempts = 0L;

    @OneToOne(mappedBy = "user")
    private UserProfile userProfile;

    /**
     * TODO: Phase v1.0.2 upgrade Roles base support:
     * USER, AMDIN, SWAGGER, DEVELOPER
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override // using for security only
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userStatus.equals(UserStatus.ACTIVE);
    }

    @Override
    public Long getAccountId() {
        return id;
    }
}