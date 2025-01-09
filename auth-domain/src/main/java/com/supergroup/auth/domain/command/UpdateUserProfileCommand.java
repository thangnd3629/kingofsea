package com.supergroup.auth.domain.command;

import com.supergroup.auth.domain.model.UserProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpdateUserProfileCommand {
    private String      username;
    private Long        avatarId;
    private UserProfile userProfile;
}
