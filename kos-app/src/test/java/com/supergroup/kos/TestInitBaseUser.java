package com.supergroup.kos;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.supergroup.auth.domain.model.User;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.building.CastleBuilding;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingRepository;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;

@SpringBootTest()
@Transactional
@ActiveProfiles("test")
public class TestInitBaseUser {
    @Autowired
    private KosProfileService kosProfileService;
    @Autowired
    private SeaElementService seaElementService;
    @Autowired
    private UserBaseService    userBaseService;
    @Autowired
    private BuildingRepository buildingRepository;
    @Autowired
    private SetUpDataTest      setUpDataTest ;
    private KosProfile kosProfile= null;
    private UserBase          userBaseFree = null;

    @BeforeEach
    public void setup() throws IOException {
        User user = setUpDataTest.createUser();
        this.kosProfile = setUpDataTest.createKosProfile(user);
        // add user base
        UserBase userBase = new UserBase();
        userBase.setActive(false)
                .setCoordinate(new Coordinates(1L, 1L));
        seaElementService.saveToDatabase(userBase);
        // userBaseFree
        this.userBaseFree = userBaseService.getUserBaseFree();
        var castle = new CastleBuilding().setIdlePeople(0D)
                                         .setLastTimeClaim(LocalDateTime.now())
                                         .setKosProfile(kosProfile)
                                         .setLevel(1L)
                                         .setName(BuildingName.CASTLE);
        buildingRepository.save(castle);
        kosProfileService.initBaseUser(kosProfile);
    }

    @Test
    public void test_init_base_1() {
        UserBase userBaseCheck = userBaseService.getById(this.userBaseFree.getId());
        Assertions.assertEquals(userBaseCheck.getKosProfile().getId(), kosProfile.getId());
    }

    @Test
    public void test_init_base_2() {
        kosProfileService.initBaseUser(kosProfile);
        UserBase userBaseCheck = userBaseService.getById(this.userBaseFree.getId());

        // double init
        kosProfileService.initBaseUser(kosProfile);
        Assertions.assertEquals(userBaseCheck.getKosProfile().getId(), kosProfile.getId());
    }

}
