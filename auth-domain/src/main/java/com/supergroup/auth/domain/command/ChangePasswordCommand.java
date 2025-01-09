package com.supergroup.auth.domain.command;

import com.supergroup.auth.domain.model.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ChangePasswordCommand {
    private final String oldPassword;
    private final String newPassword;
    private final User   user;
}
