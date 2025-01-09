package com.supergroup.kos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.supergroup.kos.building.domain.constant.seamap.ResourceIslandType;
import com.supergroup.kos.building.domain.constant.seamap.ZoneSeaType;
import com.supergroup.kos.building.domain.model.config.seamap.BossSeaConfig;
import com.supergroup.kos.building.domain.model.config.seamap.ElementAccording;
import com.supergroup.kos.building.domain.model.config.seamap.ElementAccordingZone;
import com.supergroup.kos.building.domain.model.config.seamap.ResourceIslandConfig;
import com.supergroup.kos.building.domain.model.config.seamap.SeaElementConfig;
import com.supergroup.kos.building.domain.model.config.seamap.SeaMapConfig;
import com.supergroup.kos.building.domain.model.config.seamap.SizeZoneSeaConfig;
import com.supergroup.kos.building.domain.model.config.seamap.ZoneSeaConfig;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.OccupiedArea;
import com.supergroup.kos.building.domain.model.seamap.RefreshNpcAndMineResult;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementConfigRepository;
import com.supergroup.kos.building.domain.service.seamap.RefreshNpcAndMineService;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;

@SpringBootTest()
@Transactional
public class TestRefreshElement {
    @Autowired
    private SetUpDataTest            setUpDataTest;
    @Autowired
    private RefreshNpcAndMineService refreshNpcAndMineService;
    @Autowired
    private SeaElementService          seaElementService;
    @Autowired
    private SeaElementConfigRepository<SeaElementConfig> seaElementConfigRepository;

    @BeforeEach
    public void setup() throws IOException {
        setUpDataTest.deleteAllConfig();
//        seaElementService.deleteAll();
        seaElementConfigRepository.deleteAll();
        // create elementConfig
        List<SeaElementConfig> seaElementConfigList = new ArrayList<>();
        BossSeaConfig bossSeaConfig = (BossSeaConfig) new BossSeaConfig().setLevel(1L).setOccupied(new OccupiedArea().setHeight(1L)
                                                                                                                               .setLength(1L)
                                                                                                                               .setWidth(1L));
        ResourceIslandConfig resourceIslandConfigStone = (ResourceIslandConfig) new ResourceIslandConfig().setLevel(1L).setOccupied(
                new OccupiedArea().setHeight(1L).setLength(1L)
                                  .setWidth(1L));
        resourceIslandConfigStone.setResourceType(ResourceIslandType.STONE);
        ResourceIslandConfig resourceIslandConfigWood = (ResourceIslandConfig) new ResourceIslandConfig().setLevel(1L).setOccupied(
                new OccupiedArea().setHeight(1L).setLength(1L)
                                  .setWidth(1L));
        resourceIslandConfigWood.setResourceType(ResourceIslandType.WOOD);

        seaElementConfigList.add(bossSeaConfig);
        seaElementConfigList.add(resourceIslandConfigStone);
        seaElementConfigList.add(resourceIslandConfigWood);
        var listConfigElement = setUpDataTest.createListSeaElementConfig(seaElementConfigList);

        // create ZoneSea
        ZoneSeaConfig zoneSeaConfig = new ZoneSeaConfig();
        zoneSeaConfig.setRadius(1000L);
        List<SizeZoneSeaConfig> list = new ArrayList<>();
        list.add(new SizeZoneSeaConfig().setType(ZoneSeaType.ZONE_ONE).setRadius(500L));
        list.add(new SizeZoneSeaConfig().setType(ZoneSeaType.ZONE_TWO).setRadius(800L));
        list.add(new SizeZoneSeaConfig().setType(ZoneSeaType.ZONE_THREE).setRadius(1000L));
//        setUpDataTest.createZoneSeaConfig(zoneSeaConfig);
        // create SeaConfig
        SeaMapConfig seaMapConfig = new SeaMapConfig();
        List<ElementAccording> elementAccordingList = new ArrayList<>();
        elementAccordingList.add(new ElementAccording().setElementConfigId(2L).setQuantity(2L));
        elementAccordingList.add(new ElementAccording().setElementConfigId(3L).setQuantity(2L));
        elementAccordingList.add(new ElementAccording().setElementConfigId(4L).setQuantity(2L));
        seaMapConfig.setElementAccordingBaseUser(elementAccordingList);
        //
        List<ElementAccordingZone> elementAccordingZoneList = new ArrayList<>();
        elementAccordingZoneList.add(new ElementAccordingZone().setZoneSeaType(ZoneSeaType.ZONE_ONE).setElements(elementAccordingList));
        elementAccordingZoneList.add(new ElementAccordingZone().setZoneSeaType(ZoneSeaType.ZONE_TWO).setElements(elementAccordingList));
        elementAccordingZoneList.add(new ElementAccordingZone().setZoneSeaType(ZoneSeaType.ZONE_THREE).setElements(elementAccordingList));

        setUpDataTest.createSeaMapConfig(seaMapConfig);

        // create UserBaseActive
        UserBase userBase = new UserBase();
        userBase.setId(1L).setIsRefreshable(false).setActive(true).setCoordinate(new Coordinates().setX(0L).setY(0L));
        userBase.setNpcElements("[(-630;16), (-628;22), (-632;13), (-633;21), (-650;9), (-627;10), (-641;23), (-641;21), (-640;1), (-637;4)]");
//        setUpDataTest.createElements(userBase);

        // create Elements test
//        setUpDataTest.createElements(new BossSea().setSeaElementConfig(bossSeaConfig).setIsRefreshable(true));


    }

    @Test
    public void test_refresh_npc_elements() {
        RefreshNpcAndMineResult result = refreshNpcAndMineService.refreshNpcAndMine();
    }

}
