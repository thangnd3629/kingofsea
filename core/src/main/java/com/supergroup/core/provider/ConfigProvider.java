package com.supergroup.core.provider;

import org.springframework.stereotype.Component;

import com.supergroup.core.constant.ConfigKey;
import com.supergroup.core.model.Config;
import com.supergroup.core.service.ConfigService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConfigProvider {
    private final ConfigService configService;

    public Config getConfig(ConfigKey configKey) {
        return configService.findConfigByKey(configKey);
    }
}
