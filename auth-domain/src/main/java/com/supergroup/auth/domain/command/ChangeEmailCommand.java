package com.supergroup.auth.domain.command;

import com.supergroup.auth.domain.model.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ChangeEmailCommand {
    private final String newEmail;
    private final User   user;
}
