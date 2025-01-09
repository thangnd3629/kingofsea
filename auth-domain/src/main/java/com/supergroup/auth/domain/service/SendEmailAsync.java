package com.supergroup.auth.domain.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.supergroup.auth.domain.constant.VerifyReason;
import com.supergroup.auth.domain.model.Registration;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SendEmailAsync {

    private final AuthEmailService authEmailService;

    @Async
    public void sendRegisterVerifyOTP(Registration registration, String otp) {
        authEmailService.sendVerifyCode(registration.getEmail(), otp, registration.getUsername(), VerifyReason.REGISTER);
    }

    @Async
    public void sendForgotPasswordVerifyByEmail(String email, String username, String otp) {
        authEmailService.sendVerifyCode(email, otp, username, VerifyReason.FORGOT_PASSWORD);
    }

    @Async
    public void sendChangeEmailVerifyByEmail(String email, String username, String otp) {
        authEmailService.sendVerifyCode(email, otp, username, VerifyReason.CHANGE_EMAIL);
    }
}
