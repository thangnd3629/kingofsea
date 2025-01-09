package com.supergroup.kos.cronjob;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "sendgrid")
@Getter
@Setter
class SendgridProperties {
    private String masterEmail;
    private String token;
}
