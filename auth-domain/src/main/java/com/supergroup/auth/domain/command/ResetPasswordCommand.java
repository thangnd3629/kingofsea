package com.supergroup.auth.domain.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ResetPasswordCommand {
    private final String verifyToken;
    private final String newPassword;
    private final String otp;
}
