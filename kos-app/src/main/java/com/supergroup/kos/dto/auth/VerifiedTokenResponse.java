package com.supergroup.kos.dto.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class VerifiedTokenResponse {
    private String username;
}
