package com.supergroup.admin.domain.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class AdminLoginCommand {
    private final String username;
    private final String password;
}
