package com.supergroup.auth.domain.constant;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum UserStatus {

    ACTIVE, BANNED, DELETED;

    public static UserStatus of(String key) {
        try {
            return UserStatus.valueOf(key);
        }
        catch (Exception e) {
            throw KOSException.of(ErrorCode.STATUS_NOT_FOUND);
        }
    }
}
