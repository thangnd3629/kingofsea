package com.supergroup.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication(scanBasePackages = { "com.supergroup.*" })
@ComponentScan({ "com.supergroup.*" })
@EntityScan(basePackages = "com.supergroup.*")
@EnableConfigurationProperties
@ConfigurationPropertiesScan("com.supergroup.*")
@EnableRedisRepositories(considerNestedRepositories = true,
                         basePackages = { "com.supergroup.kos.building.domain.repository.cache.**" })
@EnableJpaAuditing
@EnableJpaRepositories(considerNestedRepositories = true,
                       basePackages = {
                               "com.supergroup.kos.building.domain.repository.persistence.**",
                               "com.supergroup.auth.domain.repository.persistence.**",
                               "com.supergroup.kos.notification.domain.repository.**",
                               "com.supergroup.admin.domain.repository.**",
                               "com.supergroup.core.**"
                       })
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
