package com.supergroup.kos.cliapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import com.supergroup.kos.building.domain.model.profile.KosProfile;

@SpringBootApplication(scanBasePackages = { "com.supergroup.*" })
@ComponentScan({ "com.supergroup.*" })
@EntityScan(basePackages = "com.supergroup.*", basePackageClasses = { KosProfile.class })
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
                               "com.supergroup.core.**",
                               "com.supergroup.admin.domain.repository.**"
                       })
public class CliAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CliAppApplication.class, args);
    }
}
