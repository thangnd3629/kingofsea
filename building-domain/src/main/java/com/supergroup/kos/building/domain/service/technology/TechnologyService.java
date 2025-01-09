package com.supergroup.kos.building.domain.service.technology;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.TechnologyCode;
import com.supergroup.kos.building.domain.model.technology.Technology;
import com.supergroup.kos.building.domain.repository.persistence.technology.TechnologyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TechnologyService {

    private final TechnologyRepository technologyRepository;

    public Technology findByCode(TechnologyCode code) {
        return technologyRepository.findByCode(code)
                                   .orElseThrow(() -> KOSException.of(ErrorCode.TECHNOLOGY_NOT_FOUND));
    }

    public Technology findTechnologyUnlockMilitaryAndAdvancedTech() {
        return technologyRepository.findByCode(TechnologyCode.SC5)
                                   .orElseThrow(() -> KOSException.of(ErrorCode.TECHNOLOGY_NOT_FOUND));
    }

    public Technology findByUnlockBuildingName(BuildingName name) {
        return technologyRepository.findAll().stream().filter(technology -> Objects.nonNull(technology.getUnLockListBuildingName())
                                                                            && technology.getUnLockListBuildingName().contains(name))
                                   .findFirst()
                                   .orElse(null);
    }
}
