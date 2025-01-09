package com.supergroup.auth.domain.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class VerifyRegistrationCommand {
    private final String token;
    private final String otp;
}
