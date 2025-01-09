package com.supergroup.kos.dto.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.supergroup.auth.domain.validator.InputFieldValidator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequest {
    @Email(message = "Email is not valid",
           regexp = InputFieldValidator.EMAIL_REGEX)
    @NotEmpty(message = "Email cannot be empty")
    @NotBlank(message = "Email is required.")
    @NotNull
    private String email;
}
