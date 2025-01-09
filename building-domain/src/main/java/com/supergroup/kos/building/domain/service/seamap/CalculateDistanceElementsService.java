package com.supergroup.kos.building.domain.service.seamap;

import org.springframework.stereotype.Service;

import com.supergroup.core.utils.Distance;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
@Service
public class CalculateDistanceElementsService {
    private final UserBaseService      userBaseService;

    public Long calculateDistanceBetweenElement(SeaElement seaElement1, SeaElement seaElement2) {
        return Math.round(Distance.calculateDistance(seaElement1.getX(), seaElement1.getY(), seaElement2.getX(), seaElement2.getY()));
    }

    public Long calculateDistanceBetweenBaseUser(Long kosProfile1, Long kosProfile2) {
        return  calculateDistanceBetweenElement(userBaseService.getByKosProfileId(kosProfile1), userBaseService.getByKosProfileId(kosProfile2));
    }
}
