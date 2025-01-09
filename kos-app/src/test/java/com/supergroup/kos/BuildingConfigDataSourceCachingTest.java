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

import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.CastleConfig;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.building.CastleConfigRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(properties = { "seamap.parcel-size=40", "seamap.size=2000" })
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@Transactional
@Slf4j
public class BuildingConfigDataSourceCachingTest {

    @Autowired
    private BuildingConfigDataSource buildingConfigDataSource;

    @Autowired
    private CastleConfigRepository castleConfigRepository;

    @Test
    @Order(1)
    public void test_get_building_config() {

        // save config
        var config = new CastleConfig();
        config.setLevel(1L)
              .setName(BuildingName.CASTLE);
        config.setPopulationGrowthBase(0D)
              .setMaxPopulation(0L)
              .setMpMultiplier(0D)
              .setInstant(0L);
        castleConfigRepository.save(config);

        // test caching
        log.info("First time get config");
        var castleConfig = buildingConfigDataSource.getConfig(BuildingName.CASTLE, 1L);
        Assertions.assertNotNull(castleConfig);
        log.info("Second time get config");
        castleConfig = buildingConfigDataSource.getConfig(BuildingName.CASTLE, 1L);
        Assertions.assertNotNull(castleConfig);
    }

    @Test
    @Order(2)
    public void test_get_list_building_config() {

        // save config
        var config = new CastleConfig();
        config.setLevel(1L)
              .setName(BuildingName.CASTLE);
        config.setPopulationGrowthBase(0D)
              .setMaxPopulation(0L)
              .setMpMultiplier(0D)
              .setInstant(0L);
        castleConfigRepository.saveAll(List.of(config));

        // test caching
        log.info("First time get config");
        var castleConfigs = buildingConfigDataSource.getListConfig(BuildingName.CASTLE);
        Assertions.assertNotNull(castleConfigs);
        log.info("Second time get config");
        castleConfigs = buildingConfigDataSource.getListConfig(BuildingName.CASTLE);
        Assertions.assertNotNull(castleConfigs);
    }

}
