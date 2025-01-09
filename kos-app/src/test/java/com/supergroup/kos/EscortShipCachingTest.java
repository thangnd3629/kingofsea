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

import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.model.config.EscortShipConfig;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipConfigRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(properties = { "seamap.parcel-size=40", "seamap.size=2000" })
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@Transactional
@Slf4j
public class EscortShipCachingTest {

    @Autowired
    private EscortShipConfigRepository repository;
    @Autowired
    private EscortShipConfigDataSource escortShipConfigDataSource;

    @Test
    @Order(1)
    public void test_get_relic_config() {
        // save weapon set config
        var config = new EscortShipConfig();
        config.setType(EscortShipType.AGGRESSOR);
        config = repository.save(config);

        // test caching
        log.info("First time get ship config");
        var weaponSetConfig = escortShipConfigDataSource.getByType(EscortShipType.AGGRESSOR);
        Assertions.assertNotNull(weaponSetConfig);
        log.info("Second time get ship config");
        escortShipConfigDataSource.getByType(EscortShipType.AGGRESSOR);
    }

    @Test
    @Order(1)
    public void test_get_all_relic_config() {
        // test caching
        log.info("First time get all ship config");
        var weaponSetConfig = escortShipConfigDataSource.getAll();
        Assertions.assertNotNull(weaponSetConfig);
        log.info("Second time get all ship config");
        escortShipConfigDataSource.getAll();
    }
}
