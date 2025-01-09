package com.supergroup.kos.dto.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.supergroup.auth.domain.validator.InputFieldValidator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotNull
    @NotBlank(message = "Password is required.")
    @NotEmpty(message = "Password is required.")
    @Pattern(regexp = InputFieldValidator.PASSWORD_REGEX)
    private String password;

    @Email(message = "Email is not valid",
           regexp = InputFieldValidator.EMAIL_REGEX)
    @NotEmpty(message = "Email cannot be empty")
    @NotBlank(message = "Email is required.")
    @NotNull
    private String email;
    @NotEmpty(message = "Username cannot be empty")
    @NotBlank(message = "Username is required.")
    @Pattern(regexp = InputFieldValidator.USERNAME_REGEX)
    @NotNull
    private String username;

}
