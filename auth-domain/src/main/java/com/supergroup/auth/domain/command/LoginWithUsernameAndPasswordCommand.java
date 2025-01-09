package com.supergroup.auth.domain.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginWithUsernameAndPasswordCommand {
    private final String email;
    private final String rawPassword;
}
