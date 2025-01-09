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

import com.supergroup.kos.building.domain.model.config.WeaponSetConfig;
import com.supergroup.kos.building.domain.repository.persistence.weapon.WeaponSetConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.weapon.WeaponSetConfigRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(properties = { "seamap.parcel-size=40", "seamap.size=2000" })
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@Transactional
@Slf4j
public class WeaponSetConfigCachingTest {

    @Autowired
    private WeaponSetConfigRepository repository;
    @Autowired
    private WeaponSetConfigDataSource weaponSetConfigDataSource;

    @Test
    @Order(1)
    public void test_get_weapon_set_config() {
        // save weapon set config
        var config = new WeaponSetConfig();
        config.setId(1L);
        config.setGold(0L);
        config.setName("Test Weapon Set");
        config = repository.save(config);

        // test caching
        log.info("First time get weapon set config");
        var weaponSetConfig = weaponSetConfigDataSource.getById(config.getId());
        Assertions.assertNotNull(weaponSetConfig);
        log.info("Second time get weapon set config");
        weaponSetConfigDataSource.getById(config.getId());
    }

    @Test
    @Order(2)
    public void test_get_all_weapon_set_config() {
        // save weapon set config
        var config = new WeaponSetConfig();
        config.setId(2L);
        config.setGold(0L);
        config.setName("Test Weapon Set");
        repository.saveAll(List.of(config));

        // test caching
        log.info("First time get weapon set config");
        var weaponSetConfig = weaponSetConfigDataSource.getAll();
        Assertions.assertNotNull(weaponSetConfig);
        log.info("Second time get weapon set config");
        weaponSetConfigDataSource.getAll();
    }
}
