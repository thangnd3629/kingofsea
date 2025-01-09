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

import com.supergroup.kos.building.domain.model.config.QueenConfig;
import com.supergroup.kos.building.domain.repository.persistence.queen.QueenConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.queen.QueenConfigRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(properties = { "seamap.parcel-size=40", "seamap.size=2000" })
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@Transactional
@Slf4j
public class QueenConfigCachingTest {

    @Autowired
    private QueenConfigRepository repository;
    @Autowired
    private QueenConfigDataSource queenConfigDataSource;

    @Test
    @Order(1)
    public void test_get_queen_config() {
        // save weapon set config
        var config = new QueenConfig();
        config.setId(1L);
        config.setName("Test Queen");
        config.setThumbnail("https://test.png");
        config = repository.save(config);

        // test caching
        log.info("First time get queen config");
        var queenConfig = queenConfigDataSource.getById(config.getId());
        Assertions.assertNotNull(queenConfig);
        log.info("Second time get queen config");
        queenConfigDataSource.getById(config.getId());
    }

    @Test
    @Order(2)
    public void test_get_all_queen_config() {
        // save weapon set config
        var config = new QueenConfig();
        config.setId(1L);
        config.setName("Test Queen");
        config.setThumbnail("https://test.png");
        repository.saveAll(List.of(config));

        // test caching
        log.info("First time get queen config");
        var queenConfig = queenConfigDataSource.getAll();
        Assertions.assertNotNull(queenConfig);
        log.info("Second time get queen config");
        queenConfigDataSource.getAll();
    }
}
