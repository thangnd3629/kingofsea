package com.supergroup.kos.building.domain.service.queen;

import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetOwnQueensCommand;
import com.supergroup.kos.building.domain.model.config.QueenConfig;
import com.supergroup.kos.building.domain.repository.persistence.queen.QueenConfigRepository;
import com.supergroup.kos.building.domain.service.building.QueenBuildingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueenConfigService {

    private final QueenConfigRepository queenConfigRepository;
    private final QueenBuildingService  queenBuildingService;

    public QueenConfig findById(Long id) {
        return queenConfigRepository.findById(id).orElseThrow(() -> KOSException.of(ErrorCode.QUEEN_MODEL_NOT_FOUND));
    }

    public Boolean isExist(Long kosProfileId, Long queenConfigId) {
        var ownQueens = queenBuildingService.getOwnQueens(new GetOwnQueensCommand(kosProfileId));
        return ownQueens.stream().anyMatch(queen -> queen.getQueenConfig().getId().equals(queenConfigId));
    }

}
