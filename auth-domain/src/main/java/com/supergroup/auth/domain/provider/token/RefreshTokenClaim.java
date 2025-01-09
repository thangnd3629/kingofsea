package com.supergroup.auth.domain.provider.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RefreshTokenClaim {
    private final Long userId;
    private final Long sessionId;
}
