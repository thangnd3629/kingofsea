package com.supergroup.cache.redis;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RedisCacheStorage<T> implements CacheStorage<T> {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(String key, T t) {
        redisTemplate.opsForValue().set(key, t);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public Optional<T> findByKey(String key) {
        T t = (T) redisTemplate.opsForValue().get(key);
        if (t != null) {
            return Optional.of(t);
        }
        return Optional.empty();
    }

    @Override
    public T updateByKey(String key, T t) {
        redisTemplate.opsForValue().set(key, t);
        return t;
    }

    @Override
    public void saveWithTimeout(String key, T t, Long timeOut, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, t, timeOut, timeUnit);
    }

}
