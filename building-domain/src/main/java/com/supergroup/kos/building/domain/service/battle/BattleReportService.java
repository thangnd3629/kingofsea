package com.supergroup.kos.building.domain.service.battle;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.BattleReportServiceAsyncTask;
import com.supergroup.kos.building.domain.constant.BattleProfileType;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.dto.battle.GetBattleByPageCommand;
import com.supergroup.kos.building.domain.model.battle.BattleReport;
import com.supergroup.kos.building.domain.model.battle.RoundReport;
import com.supergroup.kos.building.domain.model.battle.UserBattleReport;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.RoundReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.UserBattleReportRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Service
@RequiredArgsConstructor
public class BattleReportService {

    @Delegate
    private final BattleReportRepository battleReportRepository;
    private final RoundReportRepository        roundReportRepository;
    private final UserBattleReportRepository   userBattleReportRepository;
    private final BattleReportServiceAsyncTask battleReportServiceAsyncTask;

    /**
     * after round, save report and it to users
     */
    @Transactional
    public void sendRoundReport(RoundReport roundReport) {
        try {
            // send to notification for joiners
            var rr = roundReportRepository.findById(roundReport.getId())
                                          .orElseThrow(() -> KOSException.of(ErrorCode.BATTLE_REPORT_NOT_FOUND));
            battleReportServiceAsyncTask.sendRoundReportNotification(rr, rr.getRound()
                                                                           .getBattle()
                                                                           .getBattleProfiles()
                                                                           .stream()
                                                                           .filter(v -> v.getType().equals(BattleProfileType.USER))
                                                                           .map(battleProfile -> battleProfile.getKosProfile().getUser().getId())
                                                                           .collect(Collectors.toSet()));
            roundReportRepository.save(roundReport);
        } catch (Exception ignored) {}
    }

    /**
     * after battle, save report and it to users
     */
    @Transactional
    public void sendBattleReport(BattleReport battleReport) {
        battleReportServiceAsyncTask.sendBattleReportNotification(battleReport,
                                                                  battleReport.getJoiners()
                                                                              .stream()
                                                                              .filter(v -> v.getType().equals(BattleProfileType.USER))
                                                                              .map(b -> b.getKosProfile().getUser())
                                                                              .collect(Collectors.toList()));
        battleReportRepository.save(battleReport);
    }

    @Transactional
    public BattleReport getBattleReportByBattleIdAndKosProfileId(Long id, Long kosProfileId) {
        return battleReportRepository.findByBattleIdAndKosProfileId(id, kosProfileId)
                                     .orElseThrow(() -> KOSException.of(ErrorCode.BATTLE_REPORT_NOT_FOUND));
    }

    @Transactional
    public Page<BattleReport> getBattleReportByPage(GetBattleByPageCommand command) {
        return userBattleReportRepository.findByKosProfileIdAndBattleStatuses(command.getKosProfile().getId(),
                                                                              List.of(BattleStatus.END, BattleStatus.BREAK, BattleStatus.PROGRESS),
                                                                              PageRequest.of(command.getPageable().getPageNumber(),
                                                                                             command.getPageable().getPageSize(),
                                                                                             Sort.by("updatedAt").descending()));
    }

    @Transactional
    public BattleReport getBattleReportByKosProfileIdAndBattleId(Long kosProfileId, Long battleId) {
        return userBattleReportRepository.findByKosProfileIdAndBattleIdAndBattleStatuses(kosProfileId,
                                                                                         battleId,
                                                                                         List.of(BattleStatus.END,
                                                                                                 BattleStatus.BREAK,
                                                                                                 BattleStatus.PROGRESS))
                                         .orElseThrow(() -> KOSException.of(ErrorCode.BATTLE_REPORT_NOT_FOUND));
    }

    @Transactional
    public RoundReport getRoundReportById(Long roundId, Long battleId, Long kosProfileId) {
        return roundReportRepository.findByIdAndBattleIdAndKosProfileId(roundId, battleId, kosProfileId)
                                    .orElseThrow(() -> KOSException.of(ErrorCode.ROUND_REPORT_NOT_FOUND));
    }

    @Transactional
    public void deleteBattleReportByBattleId(Long battleId, Long kosProfileId) {
        var report = userBattleReportRepository.findListUserBattleReportByBattleIdAndKosProfileId(battleId, kosProfileId);
        for (UserBattleReport r : report) {
            if (r.getBattleReport().getBattle().getStatus().equals(BattleStatus.END)) {
                r.setIsDeleted(true);
                userBattleReportRepository.save(r);
            } else {
                throw KOSException.of(ErrorCode.CAN_NOT_DELETE_IN_PROGRESS_BATTLE_REPORT);
            }
        }
    }
}
