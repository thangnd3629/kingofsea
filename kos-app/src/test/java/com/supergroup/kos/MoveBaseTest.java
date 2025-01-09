package com.supergroup.kos;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.supergroup.kos.building.domain.dto.seamap.UserBaseCache;
import com.supergroup.kos.building.domain.model.building.CastleBuilding;
import com.supergroup.kos.building.domain.model.config.seamap.SeaElementConfig;
import com.supergroup.kos.building.domain.model.config.seamap.UserBaseConfig;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.OccupiedArea;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.repository.persistence.asset.AssetsRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.CastleBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.profile.KosProfileRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementConfigRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.UserBaseRepository;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;

@SpringBootTest(properties = { "seamap.parcel-size=40", "seamap.size=2000" })
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
public class MoveBaseTest {

    @Autowired
    private KosProfileRepository                         kosProfileRepository;
    @Autowired
    private UserBaseRepository                           userBaseRepository;
    @Autowired
    private UserBaseService                              userBaseService;
    @Autowired
    private SeaElementService                            seaElementService;
    @Autowired
    private CastleBuildingRepository                     castleBuildingRepository;
    @Autowired
    private AssetsRepository                             assetsRepository;
    @Autowired
    private SeaElementConfigRepository<SeaElementConfig> seaElementConfigRepository;

    @Test
    public void test_random_move_base() {
        //clean
        castleBuildingRepository.deleteAll();
        userBaseRepository.deleteAll();
        assetsRepository.deleteAll();
        kosProfileRepository.deleteAll();
        // init base
        var kosProfile = new KosProfile();
        kosProfile = kosProfileRepository.save(kosProfile);
        var castleBuilding = new CastleBuilding();
        castleBuilding.setLevel(1L);
        castleBuilding.setKosProfile(kosProfile);
        castleBuilding = castleBuildingRepository.save(castleBuilding);
        var oldUserBase = new UserBase();
        oldUserBase.setCoordinate(new Coordinates(0L, 0L));
        oldUserBase.setActive(true);
        oldUserBase.setIslandName("Test island");
        oldUserBase.setKosProfile(kosProfile);
        oldUserBase = userBaseRepository.save(oldUserBase);

        var newBase = new UserBase();
        newBase.setCoordinate(new Coordinates(1L, 1L));
        newBase.setActive(false);
        newBase.setKosProfile(null);
        newBase = userBaseRepository.save(newBase);

        userBaseService.moveBaseUserRandom(kosProfile.getId());

        // check
        var kosProfileWithNewCoor = kosProfileRepository.findById(kosProfile.getId()).get();
        Assertions.assertNotEquals(oldUserBase.getCoordinates(), kosProfileWithNewCoor.getBase().getCoordinates());
        var elementCache = (UserBaseCache) seaElementService.findByXAndYFromCache(kosProfileWithNewCoor.getBase().getCoordinates().getX(),
                                                                                  kosProfileWithNewCoor.getBase().getCoordinates().getY());
        Assertions.assertNotNull(elementCache);
        Assertions.assertEquals(kosProfileWithNewCoor.getId(), elementCache.getKosProfile().getId());
        Assertions.assertEquals(1, kosProfileWithNewCoor.getBase().getX());
        Assertions.assertEquals(1, kosProfileWithNewCoor.getBase().getY());
        Assertions.assertEquals(1, elementCache.getX());
        Assertions.assertEquals(1, elementCache.getY());
    }

    @Test
    public void test_move_base_specified() {
        //clean
        castleBuildingRepository.deleteAll();
        userBaseRepository.deleteAll();
        assetsRepository.deleteAll();
        kosProfileRepository.deleteAll();
        // init base
        var kosProfile = new KosProfile();
        kosProfile = kosProfileRepository.save(kosProfile);
        var castleBuilding = new CastleBuilding();
        castleBuilding.setLevel(1L);
        castleBuilding.setKosProfile(kosProfile);
        castleBuilding = castleBuildingRepository.save(castleBuilding);
        var config = new UserBaseConfig().setId(1L).setName("User base config");
        config.setOccupied(new OccupiedArea().setHeight(4L).setWidth(4L).setLength(4L));
        config = seaElementConfigRepository.save(config);
        var oldUserBase = new UserBase();
        oldUserBase.setCoordinate(new Coordinates(0L, 0L));
        oldUserBase.setActive(true);
        oldUserBase.setSeaElementConfig(config);
        oldUserBase.setIslandName("Test island");
        oldUserBase.setKosProfile(kosProfile);
        oldUserBase = userBaseRepository.save(oldUserBase);

        var newBase = new UserBase();
        newBase.setCoordinate(new Coordinates(100L, 100L));
        newBase.setActive(false);
        newBase.setSeaElementConfig(config);
        newBase.setKosProfile(null);
        newBase = userBaseRepository.save(newBase);

        userBaseService.moveBaseUserCertain(kosProfile.getId(), new Coordinates().setX(100L).setY(100L));

        // check
        var kosProfileWithNewCoor = kosProfileRepository.findById(kosProfile.getId()).get();
        Assertions.assertNotEquals(oldUserBase.getCoordinates(), kosProfileWithNewCoor.getBase().getCoordinates());
        var elementCache = (UserBaseCache) seaElementService.findByXAndYFromCache(kosProfileWithNewCoor.getBase().getCoordinates().getX(),
                                                                                  kosProfileWithNewCoor.getBase().getCoordinates().getY());
        Assertions.assertNotNull(elementCache);
        Assertions.assertEquals(kosProfileWithNewCoor.getId(), elementCache.getKosProfile().getId());
        Assertions.assertEquals(100, kosProfileWithNewCoor.getBase().getX());
        Assertions.assertEquals(100, kosProfileWithNewCoor.getBase().getY());
        Assertions.assertEquals(100, elementCache.getX());
        Assertions.assertEquals(100, elementCache.getY());
    }
}
