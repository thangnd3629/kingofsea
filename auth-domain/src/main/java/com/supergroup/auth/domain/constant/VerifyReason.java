package com.supergroup.auth.domain.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum VerifyReason {

    REGISTER("Register"),
    FORGOT_PASSWORD("Forgot password"),
    UPDATE_EMAIL("Update email"),
    CHANGE_EMAIL("Change email");

    private String name;

}
