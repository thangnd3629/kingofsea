package com.supergroup.kos.building.domain.service.seamap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.repository.persistence.seamap.UserBaseRepository;
import com.supergroup.kos.building.domain.utils.SeaMapCoordinatesUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CheckCoordinatesElementSeaMapService {
    private final UserBaseRepository userBaseRepository;
    private final SeaElementService seaElementService;

    public Boolean checkCoordinates(Boolean allMap) { // true : UserBase, boss, resource active = (true, false)

        List<UserBase> userBaseList = new ArrayList<>();
        var listTypes = List.of(SeaElementType.BOSS, SeaElementType.RESOURCE);
        List<SeaElement> npcAndResource = seaElementService.findSeaElementByTypeFromDatabase(listTypes);
        if(allMap) {
            userBaseList = userBaseRepository.findAll();
        } else {
            userBaseList = userBaseRepository.findByActive(true);
        }

        Map<String, String> coordinatesUsed = new HashMap<>();
        for (UserBase userBase : userBaseList) {
            Coordinates coordinates = userBase.getCoordinates();
            coordinatesUsed.put(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX(), coordinates.getY()), null);
            coordinatesUsed.put(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX(), coordinates.getY() + 1), null);
            coordinatesUsed.put(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX() + 1, coordinates.getY()),null);
            coordinatesUsed.put(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX() + 1, coordinates.getY() + 1),null);
        }

        for (SeaElement seaElement : npcAndResource) {
            Coordinates coordinates = seaElement.getCoordinates();
            coordinatesUsed.put(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX(), coordinates.getY()), null);
        }
        Integer totalCoordinates = userBaseList.size() * 4 + npcAndResource.size();
         return (coordinatesUsed.keySet().size() == totalCoordinates);
    }
}
