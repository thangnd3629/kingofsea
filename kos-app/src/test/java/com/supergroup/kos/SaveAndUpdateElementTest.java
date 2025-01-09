package com.supergroup.kos;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.kos.building.domain.command.SaveOrUpdateElementCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.dto.seamap.UserBaseCache;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.building.CastleBuilding;
import com.supergroup.kos.building.domain.model.config.seamap.SeaElementConfig;
import com.supergroup.kos.building.domain.model.config.seamap.UserBaseConfig;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.repository.cache.seamap.SeaElementCacheRepository;
import com.supergroup.kos.building.domain.repository.persistence.asset.AssetsRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.CastleBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.profile.KosProfileRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementConfigRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementRepository;
import com.supergroup.kos.building.domain.service.seamap.MapService;

@SpringBootTest(properties = { "seamap.parcel-size=100", "seamap.size=2000" })
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@Transactional
public class SaveAndUpdateElementTest {

    @Autowired
    private MapService                                mapService;
    @Autowired
    private SeaElementCacheRepository                 seaElementCacheRepository;
    @Autowired
    private CastleBuildingRepository                  castleBuildingRepository;
    @Autowired
    private KosProfileRepository                      kosProfileRepository;
    @Autowired
    private AssetsRepository                          assetsRepository;
    @Autowired
    private SeaElementConfigRepository<SeaElementConfig> seaElementConfigRepository;
    @Autowired
    private SeaElementRepository<SeaElement> seaElementRepository;

    @BeforeEach
    public void setup() {
        seaElementCacheRepository.deleteAll();
        seaElementRepository.deleteAll();
        castleBuildingRepository.deleteAll();
        assetsRepository.deleteAll();
        seaElementConfigRepository.deleteAll();
        kosProfileRepository.deleteAll();
    }

    @Test
    public void save_complex_element() {

        var kosProfile = new KosProfile().setId(1L);
        kosProfile = kosProfileRepository.save(kosProfile);
        var asset = new Assets().setId(1L);
        asset = assetsRepository.save(asset);

        kosProfile.setAssets(asset);
        asset.setKosProfile(kosProfile);

        kosProfile = kosProfileRepository.save(kosProfile);
        asset = assetsRepository.save(asset);
        var castleBuilding = new CastleBuilding();
        castleBuilding.setLevel(1L)
                      .setName(BuildingName.CASTLE)
                      .setKosProfile(kosProfile);
        castleBuildingRepository.save(castleBuilding);

        var config = new UserBaseConfig();
        config.setId(0L).setName("save_complex_element");
        config = seaElementConfigRepository.save(config);

        var element = new UserBase();
        element.setCoordinate(new Coordinates().setX(100L).setY(100L));
        element.setSeaElementConfig(config);
        element.setKosProfile(kosProfile);

        var updateCommand = new SaveOrUpdateElementCommand(element);

        var savedElement = mapService.saveOrUpdateElement(updateCommand);
        var cacheElement = (UserBaseCache) seaElementCacheRepository.findById(savedElement.getId()).get();
        Assertions.assertEquals(1, savedElement.getParcelX());
        Assertions.assertEquals(1, savedElement.getParcelY());
        Assertions.assertEquals(1, cacheElement.getKosProfile().getLevel());
        Assertions.assertEquals(savedElement.getSeaElementConfig().getId(), cacheElement.getSeaElementConfig().getId());
    }

    @Test
    public void save_element_with_coordinate_100_100() {
        var element = new UserBase();
        element.setCoordinate(new Coordinates().setX(100L).setY(100L));
        var updateCommand = new SaveOrUpdateElementCommand(element);
        var savedElement = mapService.saveOrUpdateElement(updateCommand);
        Assertions.assertEquals(1, savedElement.getParcelX());
        Assertions.assertEquals(1, savedElement.getParcelY());
    }

    @Test
    public void save_element_with_coordinate_200_200() {
        var element = new UserBase();
        element.setCoordinate(new Coordinates().setX(200L).setY(200L));
        var updateCommand = new SaveOrUpdateElementCommand(element);
        var savedElement = mapService.saveOrUpdateElement(updateCommand);
        Assertions.assertEquals(2, savedElement.getParcelX());
        Assertions.assertEquals(2, savedElement.getParcelY());
    }

    @Test
    public void save_element_with_coordinate_100_200() {
        var element = new UserBase();
        element.setCoordinate(new Coordinates().setX(100L).setY(200L));
        var updateCommand = new SaveOrUpdateElementCommand(element);
        var savedElement = mapService.saveOrUpdateElement(updateCommand);
        Assertions.assertEquals(1, savedElement.getParcelX());
        Assertions.assertEquals(2, savedElement.getParcelY());
    }

    @Test
    public void save_element_with_coordinate_50_100() {
        var element = new UserBase();
        element.setCoordinate(new Coordinates().setX(50L).setY(100L));
        var updateCommand = new SaveOrUpdateElementCommand(element);
        var savedElement = mapService.saveOrUpdateElement(updateCommand);
        Assertions.assertEquals(1, savedElement.getParcelX());
        Assertions.assertEquals(1, savedElement.getParcelY());
    }

    @Test
    public void save_element_with_coordinate_150_100() {
        var element = new UserBase();
        element.setCoordinate(new Coordinates().setX(150L).setY(100L));
        var updateCommand = new SaveOrUpdateElementCommand(element);
        var savedElement = mapService.saveOrUpdateElement(updateCommand);
        Assertions.assertEquals(2, savedElement.getParcelX());
        Assertions.assertEquals(1, savedElement.getParcelY());
    }

    @Test
    public void save_element_with_coordinate_minus_100_minus_100() {
        var element = new UserBase();
        element.setCoordinate(new Coordinates().setX(-100L).setY(-100L));
        var updateCommand = new SaveOrUpdateElementCommand(element);
        var savedElement = mapService.saveOrUpdateElement(updateCommand);
        Assertions.assertEquals(-1, savedElement.getParcelX());
        Assertions.assertEquals(-1, savedElement.getParcelY());
    }

    @Test
    public void save_element_with_coordinate_minus_200_minus_200() {
        var element = new UserBase();
        element.setCoordinate(new Coordinates().setX(-200L).setY(-200L));
        var updateCommand = new SaveOrUpdateElementCommand(element);
        var savedElement = mapService.saveOrUpdateElement(updateCommand);
        Assertions.assertEquals(-2, savedElement.getParcelX());
        Assertions.assertEquals(-2, savedElement.getParcelY());
    }

    @Test
    public void save_element_with_coordinate_minus_100_minus_200() {
        var element = new UserBase();
        element.setCoordinate(new Coordinates().setX(-100L).setY(-200L));
        var updateCommand = new SaveOrUpdateElementCommand(element);
        var savedElement = mapService.saveOrUpdateElement(updateCommand);
        Assertions.assertEquals(-1, savedElement.getParcelX());
        Assertions.assertEquals(-2, savedElement.getParcelY());
    }

    @Test
    public void save_element_with_coordinate_minus_50_minus_100() {
        var element = new UserBase();
        element.setCoordinate(new Coordinates().setX(-50L).setY(-100L));
        var updateCommand = new SaveOrUpdateElementCommand(element);
        var savedElement = mapService.saveOrUpdateElement(updateCommand);
        Assertions.assertEquals(-1, savedElement.getParcelX());
        Assertions.assertEquals(-1, savedElement.getParcelY());
    }

    @Test
    public void save_element_with_coordinate_minus_150_minus_100() {
        var element = new UserBase();
        element.setCoordinate(new Coordinates().setX(-150L).setY(-100L));
        var updateCommand = new SaveOrUpdateElementCommand(element);
        var savedElement = mapService.saveOrUpdateElement(updateCommand);
        Assertions.assertEquals(-2, savedElement.getParcelX());
        Assertions.assertEquals(-1, savedElement.getParcelY());
    }

    @Test
    public void save_element_with_coordinate_minus_150_100() {
        var element = new UserBase();
        element.setCoordinate(new Coordinates().setX(-150L).setY(100L));
        var updateCommand = new SaveOrUpdateElementCommand(element);
        var savedElement = mapService.saveOrUpdateElement(updateCommand);
        Assertions.assertEquals(-2, savedElement.getParcelX());
        Assertions.assertEquals(1, savedElement.getParcelY());
    }

    @Test
    public void save_element_with_coordinate_150_minus_100() {
        var element = new UserBase();
        element.setCoordinate(new Coordinates().setX(150L).setY(-100L));
        var updateCommand = new SaveOrUpdateElementCommand(element);
        var savedElement = mapService.saveOrUpdateElement(updateCommand);
        Assertions.assertEquals(2, savedElement.getParcelX());
        Assertions.assertEquals(-1, savedElement.getParcelY());
    }
}
