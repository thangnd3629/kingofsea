package com.supergroup.auth.domain.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.core.model.BaseModel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_login_session", indexes = @Index(columnList = "uuid"))
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class LoginSession extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long          id;
    private LocalDateTime lastTimeRefresh;
    @Column(nullable = false)
    private String        uuid;
    private String        fcmToken;

    @Exclude
    @ManyToOne(fetch = javax.persistence.FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Transient
    private String accessToken;
    @Transient
    private String refreshToken;

}
