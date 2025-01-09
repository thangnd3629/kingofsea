package com.supergroup.admin.domain.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.supergroup.admin.domain.command.AdminUpdatePointCommand;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.model.point.Point;
import com.supergroup.kos.building.domain.repository.persistence.point.PointRepository;
import com.supergroup.kos.building.domain.service.point.PointService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminPointService {

    private final PointRepository pointRepository;
    private final PointService    pointService;

    @Transactional
    public Point update(Long kosProfileId, AdminUpdatePointCommand command) {
        var point = pointService.findByKosProfile_Id(kosProfileId)
                                .orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND));
        if (command.getGp() != null) {
            point.setGpPoint(command.getGp());
        }
        if (command.getMp() != null) {
            point.setMpPoint(command.getMp());
        }
        if (command.getTp() != null) {
            point.setTpPoint(command.getTp());
        }
        return pointRepository.save(point);
    }

}
