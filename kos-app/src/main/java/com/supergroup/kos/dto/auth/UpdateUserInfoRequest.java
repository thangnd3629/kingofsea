package com.supergroup.kos.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserInfoRequest {
    private String username;
    private Long   avatarId;
}
