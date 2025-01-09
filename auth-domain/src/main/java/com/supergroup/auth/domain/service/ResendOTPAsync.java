package com.supergroup.auth.domain.service;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.supergroup.auth.domain.constant.VerifyReason;
import com.supergroup.auth.domain.model.VerifySession;
import com.supergroup.auth.domain.repository.persistence.RegistrationRepository;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ResendOTPAsync {

    private final VerifySessionService   verifySessionService;
    private final RegistrationRepository registrationRepository;
    private final UserService            userService;
    private final AuthEmailService       authEmailService;

    @Async
    public void sendOtp(VerifySession verifySession, String username) {
        String email = getEmailByVerifySession(verifySession);
        verifySession.setLastTimeResend(LocalDateTime.now());
        verifySessionService.save(verifySession);
        authEmailService.sendVerifyCode(email, verifySession.getOtp(), username, verifySession.getReason());
    }

    @Transactional
    public String getEmailByVerifySession(VerifySession session) {
        if (session.getReason().equals(VerifyReason.REGISTER)) {
            return registrationRepository.findById(session.getAccountId()).orElseThrow(
                    () -> KOSException.of(ErrorCode.VERIFY_SESSION_INVALID)).getEmail();
        } else {
            return userService.findById(session.getAccountId()).orElseThrow(
                    () -> KOSException.of(ErrorCode.VERIFY_SESSION_INVALID)).getEmail();
        }
    }
}
