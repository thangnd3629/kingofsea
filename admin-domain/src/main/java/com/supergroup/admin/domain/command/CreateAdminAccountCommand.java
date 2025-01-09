package com.supergroup.admin.domain.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CreateAdminAccountCommand {
    private final String username;
    private final String password;
}
