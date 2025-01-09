package com.supergroup.kos.building.domain.service.technology;

import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.TechnologyCode;
import com.supergroup.kos.building.domain.model.technology.UserTechnology;
import com.supergroup.kos.building.domain.repository.persistence.technology.UserTechnologyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserTechnologyService {
    private final UserTechnologyRepository userTechnologyRepository;

    public UserTechnology findByKosProfileIdAndTechnologyCode(TechnologyCode code, Long kosProfileId) {
        return userTechnologyRepository.findByKosProfileIdAndTechnologyCode(kosProfileId, code)
                                   .orElseThrow(() -> KOSException.of(ErrorCode.TECHNOLOGY_NOT_FOUND));
    }
}
