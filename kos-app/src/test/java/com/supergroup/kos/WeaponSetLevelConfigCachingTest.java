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

import com.supergroup.kos.building.domain.constant.WeaponSetLevel;
import com.supergroup.kos.building.domain.model.config.WeaponSetLevelConfig;
import com.supergroup.kos.building.domain.repository.persistence.weapon.WeaponSetLevelConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.weapon.WeaponSetLevelConfigRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(properties = { "seamap.parcel-size=40", "seamap.size=2000" })
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@Transactional
@Slf4j
public class WeaponSetLevelConfigCachingTest {

    @Autowired
    private WeaponSetLevelConfigRepository repository;
    @Autowired
    private WeaponSetLevelConfigDataSource weaponSetLevelConfigDataSource;

    @Test
    @Order(1)
    public void test_get_weapon_set_level_config() {
        // save weapon set config
        var config = new WeaponSetLevelConfig();
        config.setId(1L);
        config.setGold(0L);
        config.setLevel(WeaponSetLevel.COMMON);
        config = repository.save(config);

        // test caching
        log.info("First time get weapon set level config");
        var weaponSetConfig = weaponSetLevelConfigDataSource.getById(config.getId());
        Assertions.assertNotNull(weaponSetConfig);
        log.info("Second time get weapon set level config");
        weaponSetLevelConfigDataSource.getById(config.getId());
    }

    @Test
    @Order(2)
    public void test_get_all_weapon_set_level_config() {
        // save weapon set config
        var config = new WeaponSetLevelConfig();
        config.setId(2L);
        config.setGold(0L);
        config.setLevel(WeaponSetLevel.COMMON);
        config = repository.save(config);
        repository.saveAll(List.of(config));

        // test caching
        log.info("First time get weapon set level config");
        var weaponSetConfig = weaponSetLevelConfigDataSource.getAll();
        Assertions.assertNotNull(weaponSetConfig);
        log.info("Second time get weapon set level config");
        weaponSetLevelConfigDataSource.getAll();
    }
}
