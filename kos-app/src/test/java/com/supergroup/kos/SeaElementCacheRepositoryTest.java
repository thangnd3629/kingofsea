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

import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.mapper.SeaElementMapper;
import com.supergroup.kos.building.domain.model.config.seamap.BossSeaConfig;
import com.supergroup.kos.building.domain.model.seamap.BossSea;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.repository.cache.seamap.SeaElementCacheRepository;

@SpringBootTest(properties = { "seamap.parcel-size=100", "seamap.size=2000" })
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@Transactional
public class SeaElementCacheRepositoryTest {

    @Autowired
    private SeaElementCacheRepository cacheRepository;
    @Autowired
    private SeaElementMapper          seaElementMapper;

    @Test
    @Order(1)
    public void test_save_boss_element() {
        var element = new BossSea().setCoordinate(new Coordinates(-1L, -2L))
                                   .setActive(true)
                                   .setParcelX(0)
                                   .setParcelY(0);
        var bossConfig = new BossSeaConfig().setBossAtk1(100L);
        element.setSeaElementConfig(bossConfig);

        var savedElement = cacheRepository.save(seaElementMapper.map(element));
        Assertions.assertNotNull(savedElement);
        Assertions.assertEquals(0, element.getParcelX());
        Assertions.assertEquals(SeaElementType.BOSS, savedElement.getSeaElementConfig().getType());
    }

    @Test
    @Order(2)
    public void test_find_boss_sea_with_coordinate() {
        var bossSea = cacheRepository.findByXAndY(-1L, -2L);

        Assertions.assertFalse(bossSea.isEmpty());
        Assertions.assertEquals(-1, bossSea.get(0).getX());
        Assertions.assertEquals(-2, bossSea.get(0).getY());
        Assertions.assertEquals(SeaElementType.BOSS, bossSea.get(0).getSeaElementConfig().getType());
    }

    @Test
    @Order(3)
    public void test_find_boss_sea_with_parcel() {
        var bossSea = cacheRepository.findByParcelXAndParcelYAndActive(0, 0, true);
        Assertions.assertFalse(bossSea.isEmpty());
        Assertions.assertEquals(-1, bossSea.get(0).getX());
        Assertions.assertEquals(-2, bossSea.get(0).getY());
        Assertions.assertEquals(SeaElementType.BOSS, bossSea.get(0).getSeaElementConfig().getType());
    }

    @Test
    @Order(4)
    public void test_find_boss_sea_by_type() {
        var bossSeas = cacheRepository.findBySeaElementConfig_Type(SeaElementType.BOSS);
        Assertions.assertFalse(bossSeas.isEmpty());
        Assertions.assertEquals(-1, bossSeas.get(0).getX());
        Assertions.assertEquals(-2, bossSeas.get(0).getY());
    }

    @Test
    @Order(4)
    public void test_delete_boss_sea_by_type() {
        cacheRepository.deleteBySeaElementConfigType(SeaElementType.BOSS);
        var bossSeas = cacheRepository.findBySeaElementConfig_Type(SeaElementType.BOSS);
        Assertions.assertTrue(bossSeas.isEmpty());
    }
}
