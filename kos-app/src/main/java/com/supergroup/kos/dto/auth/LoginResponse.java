package com.supergroup.kos.dto.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class LoginResponse {
    private final String accessToken;
    private final String refreshToken;
}
