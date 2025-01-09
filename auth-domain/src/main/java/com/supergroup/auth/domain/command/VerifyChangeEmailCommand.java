package com.supergroup.auth.domain.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class VerifyChangeEmailCommand {
    private final String verifyToken;
    private final String otp;
}
