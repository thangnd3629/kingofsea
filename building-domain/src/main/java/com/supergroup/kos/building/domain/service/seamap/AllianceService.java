package com.supergroup.kos.building.domain.service.seamap;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.model.seamap.KosAlliance;

@Service
public class AllianceService {

    public KosAlliance getUserAlliance(Long kosProfileId) {
        return null;

    }
    public boolean belongToSameAlliance(Long kosProfileId, Long allianceMemberKosProfileId){
        if (kosProfileId.equals(allianceMemberKosProfileId)){
            return true;
        }
        return false;
    }


}
