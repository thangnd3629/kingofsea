package com.supergroup.cache.redis;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface CacheStorage<T> {
    void save(String key, T t);

    void delete(String key);

    Optional<T> findByKey(String key);

    T updateByKey(String key, T t);

    void saveWithTimeout(String key, T t, Long timeOut, TimeUnit timeUnit);
}
