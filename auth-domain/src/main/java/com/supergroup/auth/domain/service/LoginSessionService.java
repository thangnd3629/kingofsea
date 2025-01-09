package com.supergroup.auth.domain.service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.supergroup.auth.domain.cache.loginsession.LoginSessionCache;
import com.supergroup.auth.domain.model.LoginSession;
import com.supergroup.auth.domain.model.User;
import com.supergroup.auth.domain.provider.token.JwtTokenProvider;
import com.supergroup.auth.domain.repository.persistence.LoginSessionCacheRepository;
import com.supergroup.auth.domain.repository.persistence.LoginSessionRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Service
@RequiredArgsConstructor
@Getter
public class LoginSessionService {

    @Delegate
    private final LoginSessionRepository      repository;
    private final LoginSessionCacheRepository cacheRepository;
    private final JwtTokenProvider            jwtTokenProvider;

    /**
     * Create new login session of user
     *
     * @param user : User want to create session
     */
    @Transactional
    public LoginSession create(User user, Long timeout, TimeUnit timeUnit) {
        var session = new LoginSession();
        var uuid = UUID.randomUUID().toString();
        session.setUuid(uuid);
        session.setUser(user);
        // save to database
        session = repository.save(session);
        // generate token
        session.setUuid(uuid)
               .setAccessToken(jwtTokenProvider.generateUserAccessToken(user.getId(), session.getId(), uuid))
               .setRefreshToken(jwtTokenProvider.generateRefreshToken(user.getId(), session.getId()));
        // save to caching storage
        var cachedSession = new LoginSessionCache().setUuid(session.getUuid())
                                                   .setUserId(session.getUser().getId());
        try {
            cacheRepository.saveLoginSession(cachedSession, timeout, timeUnit);
        } catch (Exception ex) {
            // ignore caching error
        }
        return session;
    }

    /**
     * Deactivate session by deleting
     *
     * @param id : session id
     */
    @Transactional
    public void deactivateSessionById(Long id) {
        repository.deleteById(id);
        try {
            cacheRepository.deleteById(id);
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * Deactivate session by deleting
     *
     * @param userId : user id
     */
    @Transactional
    public void deactivateAllLoginSession(Long userId) {
        repository.deleteByUserId(userId);
        try {
            cacheRepository.deleteById(userId);
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * Check if the session is active
     *
     * @param sessionUuid: session uuid
     */
    public boolean isActive(String sessionUuid) {
        try {
            if (cacheRepository.existByUuid(sessionUuid)) {
                return false;
            }
        } catch (Exception ex) {
            // ignore caching exception
            ex.printStackTrace();
        }
        return repository.existsByUuid(sessionUuid);
    }

    /**
     * Refresh session
     *
     * @param loginSession: session you want to refresh
     */
    @Transactional
    public void refresh(LoginSession loginSession) {
        var uuid = UUID.randomUUID().toString();
        loginSession.setUuid(uuid)
                    .setAccessToken(jwtTokenProvider.generateUserAccessToken(loginSession.getUser().getId(), loginSession.getId(), uuid))
                    .setRefreshToken(jwtTokenProvider.generateRefreshToken(loginSession.getUser().getId(), loginSession.getId()))
                    .setLastTimeRefresh(LocalDateTime.now());
        repository.save(loginSession);
    }

}
