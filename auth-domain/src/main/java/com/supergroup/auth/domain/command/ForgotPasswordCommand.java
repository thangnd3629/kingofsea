package com.supergroup.auth.domain.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ForgotPasswordCommand {
    private final String email;
}
