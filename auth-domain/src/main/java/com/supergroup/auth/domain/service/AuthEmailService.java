package com.supergroup.auth.domain.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.supergroup.auth.domain.constant.VerifyReason;
import com.supergroup.auth.domain.provider.EmailTemplateProvider;
import com.supergroup.email.service.EmailSender;
import com.supergroup.email.service.EmailType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthEmailService {

    private final EmailTemplateProvider emailTemplateProvider;
    private final EmailSender           emailSender;

    @Value("${website-url}")
    private String websiteUrl;

    public void sendResetPasswordLinkEmail(String token, String to, String username) {
        String subject = "KOS - Reset Password";  // TODO this is hard code!!!
        Optional<String> htmlContent = emailTemplateProvider.forgotPasswordHtml(websiteUrl + "/forgot-email?code=" + token,
                                                                                username);  // TODO this is hard code!!!
        htmlContent.ifPresent(content -> emailSender.send(subject, content, to, EmailType.HTML));
    }

    public void sendVerifyCode(String to, String otp, String username, VerifyReason verifyReason) {
        String subject = "KOS - OTP code for " + verifyReason.getName(); // TODO this is hard code!!!
        Optional<String> htmlContent = emailTemplateProvider.verifyCode(otp, username, verifyReason);
        htmlContent.ifPresent(content -> emailSender.send(subject, content, to, EmailType.HTML));
    }

    public void sendChangeEmail(String token, String to, String username) {
        String subject = "KOS - Change Email";  // TODO this is hard code!!!
        Optional<String> htmlContent = emailTemplateProvider.changeEmailHtml(websiteUrl + "/change-email?code=" + token,
                                                                                username);  // TODO this is hard code!!!
        htmlContent.ifPresent(content -> emailSender.send(subject, content, to, EmailType.HTML));
    }
}
