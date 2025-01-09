package com.supergroup.admin.domain.command;

import com.supergroup.auth.domain.constant.UserStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpdateStatusCommand {
    private Long userId;
    private UserStatus status;
}
