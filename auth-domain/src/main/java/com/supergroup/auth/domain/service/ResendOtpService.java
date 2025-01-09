package com.supergroup.auth.domain.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.supergroup.auth.domain.constant.VerifyReason;
import com.supergroup.auth.domain.model.UserProfile;
import com.supergroup.auth.domain.model.VerifySession;
import com.supergroup.auth.domain.provider.token.JwtTokenProvider;
import com.supergroup.auth.domain.repository.persistence.RegistrationRepository;
import com.supergroup.auth.domain.repository.persistence.UserProfileRepository;
import com.supergroup.auth.domain.util.OTPUtils;
import com.supergroup.core.exception.KOSException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResendOtpService {

    private final VerifySessionService   verifySessionService;
    private final RegistrationRepository registrationRepository;
    private final UserProfileRepository  userProfileRepository;
    private final JwtTokenProvider       jwtTokenProvider;
    private final ResendOTPAsync         resendOTPAsync;

    @Transactional(dontRollbackOn = KOSException.class)
    public void resendVerifyCode(String verifyToken) {
        var verifyTokenClaim = jwtTokenProvider.getVerifyTokenClaim(verifyToken);
        VerifySession verifySession = verifySessionService.checkValidVerifySession(verifyTokenClaim.getVerifyId(),
                                                                                   verifyTokenClaim.getReason());
        verifySession.setOtp(OTPUtils.code());
        verifySessionService.save(verifySession);
        var username = "";

        if (verifySession.getReason().equals(VerifyReason.REGISTER)) {
            var registration = registrationRepository.findById(verifySession.getAccountId());
            if (registration.isPresent()) {
                username = registration.get().getUsername();
            } else {
                username = "You"; // TODO hard code
            }
        } else {
            Optional<UserProfile> userProfile = userProfileRepository.findByUserId(verifySession.getAccountId());
            if (userProfile.isPresent()) {
                username = userProfile.get().getUsername();
            }
        }
        resendOTPAsync.sendOtp(verifySession, username);
    }

}
