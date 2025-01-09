package com.supergroup.auth.domain.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.auth.domain.constant.VerifyReason;
import com.supergroup.core.model.BaseModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_verify_session")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class VerifySession extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long          id;
    private LocalDateTime lastTimeResend;
    //  base on reason accountId can be registration id or user id
    private Long          accountId;
    private VerifyReason  reason;
    private String        otp;
    // number of otp attempts, if it is greater than 3, delete verify session
    private Long          attempts = 0L;
    private Long          periodTime; // min
    private LocalDateTime expirationDate;

    @Transient
    private String token;

}
