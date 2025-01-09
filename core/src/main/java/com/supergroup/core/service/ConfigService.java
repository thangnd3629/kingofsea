package com.supergroup.core.service;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ConfigKey;
import com.supergroup.core.model.Config;
import com.supergroup.core.repository.ConfigRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Service
@RequiredArgsConstructor
public class ConfigService {

    @Delegate
    private final ConfigRepository configRepository;

    @Cacheable(cacheNames = "Config", key = "#key.name()")
    public Config findConfigByKey(ConfigKey key) {
        return configRepository.findByKey(key).orElseThrow(() -> new IllegalArgumentException("Not found config with key"));
    }

    @CachePut(cacheNames = "Config", key = "#config.key.name()")
    public Config save(Config config) {
        return configRepository.save(config);
    }

}
