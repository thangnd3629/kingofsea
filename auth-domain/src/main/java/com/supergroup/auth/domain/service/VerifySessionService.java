package com.supergroup.auth.domain.service;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.supergroup.auth.domain.constant.VerifyReason;
import com.supergroup.auth.domain.model.Verifiable;
import com.supergroup.auth.domain.model.VerifySession;
import com.supergroup.auth.domain.provider.token.JwtTokenProvider;
import com.supergroup.auth.domain.repository.persistence.VerifySessionRepository;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Service
@RequiredArgsConstructor
public class VerifySessionService {

    @Delegate
    private final VerifySessionRepository repository;
    private final JwtTokenProvider        jwtTokenProvider;

    @Value("${verify-session.timeout}")
    private Long VERIFY_SESSION_TIMEOUT;
    @Value("${verify-session.period}")
    private Long VERIFY_SESSION_PERIOD;

    @Transactional
    public VerifySession createVerifySession(Verifiable verifiable, VerifyReason reason, String otp, String newEmail) {
        var existSession = this.findByAccountIdAndReason(verifiable.getAccountId(), reason);

        // check valid timeout
        if (existSession.isPresent()) {
            if (!checkValidWaitingTime(existSession.get())) {
                throw KOSException.of(ErrorCode.REQUEST_CREATE_VERIFY_INVALID);
            }
        }

        VerifySession session = existSession.orElseGet(VerifySession::new);
        var now = LocalDateTime.now();
        session.setAttempts(0L)
               .setPeriodTime(VERIFY_SESSION_PERIOD)
               .setExpirationDate(now.plusMinutes(VERIFY_SESSION_TIMEOUT))
               .setLastTimeResend(now)
               .setReason(reason)
               .setOtp(otp)
               .setAccountId(verifiable.getAccountId());
        // generate verify token
        session = this.save(session);
        var token = "";
        if (reason.equals(VerifyReason.CHANGE_EMAIL)) {
            token = jwtTokenProvider.generateChangeEmailVerifyToken(session, newEmail);
        } else {
            token = jwtTokenProvider.generateVerifyToken(session);
        }
        session.setToken(token);
        return session;
    }

    /**
     * Deactivate session by deleting
     *
     * @param session: verify session you want deactivate
     */
    @Async
    public void deactivate(VerifySession session) {
        repository.delete(session);
    }

    public VerifySession save(VerifySession verifyLoginSession) {
        return repository.save(verifyLoginSession);
    }

    @Transactional
    public boolean isExpiredSession(VerifySession verifySession) {
        if (Objects.isNull(verifySession.getExpirationDate())) {
            return false;
        } else {
            return LocalDateTime.now().isAfter(verifySession.getExpirationDate());
        }
    }

    public VerifySession checkValidVerifySession(Long id, VerifyReason verifyReason) {
        var verifySession = findByIdAndReason(id, verifyReason).orElseThrow(() -> KOSException.of(ErrorCode.VERIFY_SESSION_INVALID));
        // check valid timeout
        if (!checkValidWaitingTime(verifySession)) {
            throw KOSException.of(ErrorCode.REQUEST_CREATE_VERIFY_INVALID);
        }
        return verifySession;
    }

    private Boolean checkValidWaitingTime(VerifySession verifySession) {
        // check valid timeout
        return verifySession.getLastTimeResend().plusSeconds(verifySession.getPeriodTime()).isBefore(LocalDateTime.now());
    }
}
