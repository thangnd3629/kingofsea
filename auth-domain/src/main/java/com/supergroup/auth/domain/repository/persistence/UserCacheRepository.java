package com.supergroup.auth.domain.repository.persistence;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.supergroup.auth.domain.cache.user.UserCache;
import com.supergroup.cache.redis.CacheStorage;
import com.supergroup.cache.redis.RedisCacheStorage;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCacheRepository {

    private static final String PREFIX_KEY = "UserDetail";

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository                userRepository;

    private CacheStorage<UserCache> cacheStorage;

    @Value("${auth.cache.timeout}")
    private Long timeout;

    @PostConstruct
    public void postConstruct() {
        cacheStorage = new RedisCacheStorage<>(redisTemplate);
    }

    /**
     * get user detail from redis, if it is not existed, load it from
     * database and save it to redis
     */
    public Optional<UserDetails> getUserById(Long userId) {
        String key = buildKey(userId);
        try {
            var userDetail = cacheStorage.findByKey(key);
            if (userDetail.isPresent()) {
                return Optional.of(userDetail.get());
            } else {
                return getUserDetailFromDatabase(userId, key);
            }
        } catch (Exception ex) {
            // ingore error when get from cache fail
            // and get user detail from database
            return getUserDetailFromDatabase(userId, key);
        }
    }

    private Optional<UserDetails> getUserDetailFromDatabase(Long userId, String key) {
        var user = userRepository.findById(userId);
        if (user.isPresent()) {
            var redisUserCache = new UserCache()
                    .setId(user.get().getId())
                    .setLocked(!user.get().isEnabled())
                    .setEmail(user.get().getEmail());
            try {
                redisTemplate.opsForValue().set(key, redisUserCache, timeout, TimeUnit.MINUTES);
            } catch (Exception ex) {
                // ignore error when save to cache fail
                ex.printStackTrace();
            }
            return Optional.of(redisUserCache);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Up-to-date database and redis
     */
    public void refreshUser(Long userId) {
        String key = buildKey(userId);
        redisTemplate.delete(key);
        var user = userRepository.findById(userId);
        user.ifPresent(u -> redisTemplate.opsForValue().set(
                key,
                new UserCache().setId(u.getId())
                               .setEmail(u.getEmail())
                               .setLocked(u.isEnabled()),
                timeout,
                TimeUnit.MINUTES));
    }

    public void deleteUser(Long userId) {
        redisTemplate.delete(buildKey(userId));
    }

    private String buildKey(Long userId) {
        return String.format("%s:%d", PREFIX_KEY, userId);
    }
}
