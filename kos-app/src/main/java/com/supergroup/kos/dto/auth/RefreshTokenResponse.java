package com.supergroup.kos.dto.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RefreshTokenResponse {
    private final String accessToken;
    private final String refreshToken;
}
