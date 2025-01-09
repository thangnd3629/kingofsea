package com.supergroup.auth.domain.provider.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AccessTokenClaim {
    private final Long   userId;
    private final Long   sessionId;
    private final String uuid;
}
