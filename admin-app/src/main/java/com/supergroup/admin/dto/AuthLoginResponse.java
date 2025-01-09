package com.supergroup.admin.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AuthLoginResponse {
    private String accessToken;
}
