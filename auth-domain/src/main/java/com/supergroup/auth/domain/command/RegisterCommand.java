package com.supergroup.auth.domain.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RegisterCommand {
    private final String email;
    private final String password;
    private final String username;
}
