package com.supergroup.kos.dto.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.supergroup.auth.domain.validator.InputFieldValidator;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotNull
    @NotBlank(message = "Password is required.")
    @NotEmpty(message = "Password is required.")
    @Pattern(regexp = InputFieldValidator.PASSWORD_REGEX)
    private String newPassword;
    @NotBlank
    @NotEmpty
    @NotNull
    @Pattern(regexp = InputFieldValidator.OTP_REGEX)
    private String otp;
}
