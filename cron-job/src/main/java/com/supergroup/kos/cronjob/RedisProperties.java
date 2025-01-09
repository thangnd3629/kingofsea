package com.supergroup.kos.cronjob;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "redis")
@Getter
@Setter
class RedisProperties {
    private String  host;
    private Integer port;
    private String  password;
}