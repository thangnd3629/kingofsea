package com.supergroup.kos;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.repository.persistence.seamap.UserBaseRepository;
import com.supergroup.kos.building.domain.service.seamap.ValidUserBaseService;
import com.supergroup.kos.building.domain.utils.SeaMapCoordinatesUtils;

@SpringBootTest()
@Transactional
public class TestValidUserBase {
    @Autowired
    private UserBaseRepository   userBaseRepository;
    @Autowired
    private ValidUserBaseService validUserBaseService;

    @BeforeEach
    public void setup() throws IOException {
        userBaseRepository.deleteAll();
        UserBase userBase = (UserBase) new UserBase().setIsReady(true).setCoordinate(new Coordinates(0L, 0L)).setActive(true);
        UserBase userBaseConflict = (UserBase) new UserBase().setIsReady(false).setCoordinate(new Coordinates(1L, 0L)).setActive(true);
        userBaseRepository.save(userBase);
        userBaseRepository.save(userBaseConflict);
    }

    @Test
    public void test_valid_base_user() throws InterruptedException {
        validUserBaseService.validBaseUser();
        List<UserBase> userBaseList = userBaseRepository.findByIsReady(true);
        Assertions.assertEquals(2, userBaseList.size());
        Map<String, String> mapCoordinate = new HashMap<>();
        for (UserBase userBase : userBaseList) {
            Coordinates coordinates = userBase.getCoordinates();
            mapCoordinate.put(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX(), coordinates.getY()), null);
            mapCoordinate.put(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX(), coordinates.getY() + 1), null);
            mapCoordinate.put(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX() + 1, coordinates.getY()), null);
            mapCoordinate.put(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX() + 1, coordinates.getY() + 1), null);
        }
        Assertions.assertEquals(8, mapCoordinate.keySet().size());
    }
}
