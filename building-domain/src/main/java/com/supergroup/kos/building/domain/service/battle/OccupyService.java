package com.supergroup.kos.building.domain.service.battle;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Invader;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OccupyService {
    public boolean occupiedByAlliance(KosProfile kosProfile, SeaElement element) {
        Invader invader = element.getInvader();
        if (!element.isOccupied()) {return false;}
        // todo alliance
        return invader.getKosProfileInvader().getId().equals(kosProfile.getId());
    }
}
