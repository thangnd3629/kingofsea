package com.supergroup.kos.dto.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ForgotPasswordResponse {
    private final String verifyToken;
}
