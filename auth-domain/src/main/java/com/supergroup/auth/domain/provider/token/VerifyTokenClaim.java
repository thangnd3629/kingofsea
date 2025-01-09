package com.supergroup.auth.domain.provider.token;

import com.supergroup.auth.domain.constant.VerifyReason;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class VerifyTokenClaim {
    private final Long         accountId;
    private final Long         verifyId;
    private final VerifyReason reason;
}
