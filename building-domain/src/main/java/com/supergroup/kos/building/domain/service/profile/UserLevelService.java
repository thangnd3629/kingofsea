package com.supergroup.kos.building.domain.service.profile;

import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.repository.persistence.building.CastleBuildingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserLevelService {

    private final CastleBuildingRepository castleBuildingRepository;

    public Long getLevel(Long id) {
        return castleBuildingRepository.findByKosProfile_Id(id)
                                       .orElseThrow(() -> KOSException.of(ErrorCode.CAN_NOT_GET_LEVEL))
                                       .getLevel();
    }

}
