package com.supergroup.kos.building.domain.service.queen;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetMpFromQueenCommand;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.queen.Queen;
import com.supergroup.kos.building.domain.repository.persistence.queen.QueenRepository;
import com.supergroup.kos.building.domain.service.config.KosConfigService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueenService {

    private final QueenRepository  queenRepository;
    private final KosConfigService kosConfigService;

    public Queen save(Queen queen) {
        return queenRepository.save(queen);
    }

    public List<Queen> saveAll(List<Queen> queens) {
        return queenRepository.saveAll(queens);
    }

    public List<Queen> getQueens(Long kosProfileId) {
        return queenRepository.findByQueenBuilding_KosProfile_Id(kosProfileId);
    }

    public Queen getQueenByIdAndKosProfileId(Long queenId, Long kosProfileId) {
        return queenRepository.findByIdAndQueenBuildingKosProfileId(queenId, kosProfileId)
                              .orElseThrow(() -> KOSException.of(ErrorCode.QUEEN_NOT_FOUND));
    }

    public Long getMpFromQueens(GetMpFromQueenCommand command) {
        var kosProfileId = command.getKosProfile().getId();
        var ownQueens = getQueens(kosProfileId);
        return calcMpFromQueens(ownQueens, command.getKosProfile(), command.isIgnoreCheckOccupy());
    }

    public Long calcMpFromQueens(List<Queen> ownQueens, KosProfile kosProfile, boolean isIgnoreCheckOccupy) {
        var config = kosConfigService.occupyEffect();
        Long mp = 0L;
        var queenModelIds = new ArrayList<>();
        for (Queen queen : ownQueens) {
            if (queenModelIds.stream().noneMatch(id -> id.equals(queen.getQueenConfig().getId()))) {
                mp += queen.getQueenConfig().getMp();
            }
            queenModelIds.add(queen.getQueenConfig().getId());
        }
        // decrease mp when base occupied
        if (kosProfile.getBase().isOccupied() && !isIgnoreCheckOccupy) {
            return Math.round(mp * (1 - config.getDecreaseMp()));
        } else {
            return (long) Math.round(mp);
        }
    }

}
