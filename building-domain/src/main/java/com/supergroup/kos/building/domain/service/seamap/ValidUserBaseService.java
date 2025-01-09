package com.supergroup.kos.building.domain.service.seamap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.model.config.seamap.ZoneSeaConfig;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.repository.persistence.seamap.UserBaseRepository;
import com.supergroup.kos.building.domain.service.config.KosConfigService;
import com.supergroup.kos.building.domain.utils.SeaMapCoordinatesUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidUserBaseService {
    private final UserBaseRepository    userBaseRepository;
    private final KosConfigService      kosConfigService;

    @Transactional
    public void validBaseUser() throws InterruptedException {
        List<UserBase> userBaseConflict = userBaseRepository.findByIsReady(false);
        if (userBaseConflict.isEmpty()) {
            return;
        }
        List<UserBase> userBaseListReady = userBaseRepository.findByIsReady(true);
        Map<String, String> coordinatesFree = new HashMap<>();
        getAllCoordinatesFree(coordinatesFree);// create AllCoordinates
        for (UserBase userBase : userBaseListReady) {
            Coordinates coordinates = userBase.getCoordinates();
            coordinatesFree.remove(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX(), coordinates.getY()));
            coordinatesFree.remove(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX(), coordinates.getY() + 1));
            coordinatesFree.remove(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX() + 1, coordinates.getY()));
            coordinatesFree.remove(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX() + 1, coordinates.getY() + 1));
            Thread.sleep(100);
        }
        // repair UserBase
        ZoneSeaConfig zoneSeaConfig = kosConfigService.getZoneSeaConfig();
        Long radius = zoneSeaConfig.getRadius();
        for (UserBase userBase : userBaseConflict) {
            Coordinates coordinates = findCoordinates(radius, coordinatesFree);
            userBase.setIsReady(true).setCoordinate(coordinates);
            coordinatesFree.remove(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX(), coordinates.getY()));
            coordinatesFree.remove(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX(), coordinates.getY() + 1));
            coordinatesFree.remove(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX() + 1, coordinates.getY()));
            coordinatesFree.remove(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX() + 1, coordinates.getY() + 1));
            Thread.sleep(100);
        }
        userBaseRepository.saveAll(userBaseConflict);
    }

    private Coordinates findCoordinates(Long radius, Map<String, String> coordinatesFree) {
        Integer maxRandom = 2 * radius.intValue() - 5;
        Random random = new Random();
        Coordinates coordinates;
        while (true) {
            Boolean pass = true;
            coordinates = new Coordinates(random.nextInt(maxRandom) - radius, random.nextInt(maxRandom) - radius);
            for (long width = -5; width < 5; width++) {
                for (long height = -5; height < 5; height++) {
                    if (!coordinatesFree.containsKey(
                            SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX() + width, coordinates.getY() + height))) {
                        pass = false;
                        width = Integer.MAX_VALUE;
                        break;
                    }
                }
            }
            if (pass) {
                return coordinates;
            }
        }

    }

    private void getAllCoordinatesFree(Map<String, String> coordinatesFree) {
        ZoneSeaConfig zoneSeaConfig = kosConfigService.getZoneSeaConfig();
        for (Long x = -zoneSeaConfig.getRadius(); x <= zoneSeaConfig.getRadius() - 1; x++) {
            for (Long y = -zoneSeaConfig.getRadius(); y <= zoneSeaConfig.getRadius() - 1; y++) {
                coordinatesFree.put(SeaMapCoordinatesUtils.toStringCoordinates(x, y), null);
            }
        }
    }
}
