package com.supergroup.kos;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.constant.ConfigKey;
import com.supergroup.core.model.Config;
import com.supergroup.core.repository.ConfigRepository;
import com.supergroup.core.service.ConfigService;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(properties = { "seamap.parcel-size=40", "seamap.size=2000" })
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@Transactional
@Slf4j
public class ConfigCachingTest {

    @Autowired
    private ConfigRepository repository;
    @Autowired
    private ConfigService    configService;

    @Test
    @Order(1)
    public void test_get_relic_config() {
        // save weapon set config
        var config = new Config();
        config.setKey(ConfigKey.FREQUENCY_ASSET);
        config.setValue("100");
        config = repository.save(config);

        // test caching
        log.info("First time get relic config");
        var weaponSetConfig = configService.getById(config.getId());
        Assertions.assertNotNull(weaponSetConfig);
        log.info("Second time get relic config");
        configService.findConfigByKey(ConfigKey.FREQUENCY_ASSET);
    }
}
