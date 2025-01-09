package com.supergroup.auth.domain.repository.persistence;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.supergroup.auth.domain.cache.loginsession.LoginSessionCache;
import com.supergroup.cache.redis.CacheStorage;
import com.supergroup.cache.redis.RedisCacheStorage;

import lombok.RequiredArgsConstructor;

/**
 * Redis key for login session: LoginSession:(userId):(sessionID)
 * ex: LoginSession:102:3
 */
@Repository
@RequiredArgsConstructor
public class LoginSessionCacheRepository {

    private static final String PREFIX_KEY = "LoginSession";

    private final RedisTemplate<String, Object> redisTemplate;

    private CacheStorage<LoginSessionCache> cacheStorage;

    @PostConstruct
    private void postConstruct() {
        cacheStorage = new RedisCacheStorage<>(redisTemplate);
    }

    /**
     * Get cache login session by id
     */
    public Optional<LoginSessionCache> getById(Long sessionId) {
        return cacheStorage.findByKey(String.format("%s:*:%d", PREFIX_KEY, sessionId));
    }

    /**
     * Check the existence of user login session
     */
    public boolean existByUuid(String loginSessionUuid) {
        Optional<LoginSessionCache> loginSessionRedis = cacheStorage.findByKey(String.format("%s:*:%s", PREFIX_KEY, loginSessionUuid));
        return loginSessionRedis.isPresent();
    }

    /**
     * Save login session
     */
    public void saveLoginSession(LoginSessionCache loginSessionCache, Long timeout, TimeUnit timeUnit) {
        cacheStorage.saveWithTimeout(buildKey(loginSessionCache), loginSessionCache, timeout, timeUnit);
    }

    /**
     * Delete user login session by id
     */
    public void deleteById(Long loginSessionId) {
        cacheStorage.delete(String.format("%s:*:%d", PREFIX_KEY, loginSessionId));
    }

    /**
     * Delete all user login session
     */
    public void deleteByUserId(Long userId) {
        cacheStorage.delete(String.format("%s:%d:*", PREFIX_KEY, userId));
    }

    /**
     * Delete all user login session
     */
    public void deleteByIds(List<Long> list) {
        list.forEach(this::deleteById);
    }

    /**
     * Update login session cache
     */
    public void update(LoginSessionCache loginSession) {
        cacheStorage.updateByKey(buildKey(loginSession), loginSession);
    }

    private String buildKey(LoginSessionCache cache) {
        return String.format("%s:%d:%s", PREFIX_KEY, cache.getUserId(), cache.getUuid());
    }
}
