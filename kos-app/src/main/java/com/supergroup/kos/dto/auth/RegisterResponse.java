package com.supergroup.kos.dto.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RegisterResponse {
    private final String verifyToken;
}
