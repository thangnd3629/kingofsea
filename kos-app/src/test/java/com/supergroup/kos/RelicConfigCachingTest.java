package com.supergroup.kos;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.kos.building.domain.model.config.RelicConfig;
import com.supergroup.kos.building.domain.repository.persistence.relic.RelicConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.relic.RelicConfigRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(properties = { "seamap.parcel-size=40", "seamap.size=2000" })
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@Transactional
@Slf4j
public class RelicConfigCachingTest {

    @Autowired
    private RelicConfigRepository repository;
    @Autowired
    private RelicConfigDataSource relicConfigDataSource;

    @Test
    @Order(1)
    public void test_get_relic_config() {
        // save weapon set config
        var config = new RelicConfig();
        config.setId(1L);
        config.setName("Test Relic");
        config = repository.save(config);

        // test caching
        log.info("First time get relic config");
        var weaponSetConfig = relicConfigDataSource.getById(config.getId());
        Assertions.assertNotNull(weaponSetConfig);
        log.info("Second time get relic config");
        relicConfigDataSource.getById(config.getId());
    }

    @Test
    @Order(2)
    public void test_get_all_relic_config() {
        // save weapon set config
        var config = new RelicConfig();
        config.setId(2L);
        config.setName("Test Relic");
        repository.saveAll(List.of(config));

        // test caching
        log.info("First time get list relic config");
        var weaponSetConfig = relicConfigDataSource.getAll();
        Assertions.assertNotNull(weaponSetConfig);
        log.info("Second time get list relic config");
        relicConfigDataSource.getAll();
    }
}
