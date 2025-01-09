package com.supergroup.kos.building.domain.service.scout;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.DeleteScoutReportCommand;
import com.supergroup.kos.building.domain.command.GetScoutReportCommand;
import com.supergroup.kos.building.domain.command.UpdateScoutReportCommand;
import com.supergroup.kos.building.domain.model.scout.Scout;
import com.supergroup.kos.building.domain.model.scout.ScoutReport;
import com.supergroup.kos.building.domain.repository.persistence.scout.ScoutReportRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoutReportService {
    private final ScoutReportRepository scoutReportRepository;

    public Page<ScoutReport> getAllScoutReport(Long kosProfileId, Pageable pageable) {
        return scoutReportRepository.getAllReport(kosProfileId, pageable);
    }

    public ScoutReport findScoutReportById(Long id) {
        return scoutReportRepository.findById(id).orElseThrow(() -> KOSException.of(ErrorCode.SCOUT_REPORT_NOT_FOUND));
    }

    public ScoutReport getScoutReport(GetScoutReportCommand command) {
        ScoutReport scoutReport = findScoutReportById(command.getReportId());
        if (scoutReport.getKosProfile().getId() != command.getKosProfileId()) {
            throw KOSException.of(ErrorCode.SCOUT_REPORT_DOES_NOT_BELONG_TO_YOU);
        }
        return scoutReport;
    }

    public void updateScoutReport(UpdateScoutReportCommand command) {
        ScoutReport scoutReport = findScoutReportById(command.getScoutReportId());
        if (!scoutReport.getKosProfile().getId().equals(command.getKosProfileId())) {
            throw KOSException.of(ErrorCode.SCOUT_REPORT_DOES_NOT_BELONG_TO_YOU);
        }
        switch (command.getType()) {
            case STATUS:
                scoutReport.setIsSeen(true);
                break;
            case BOOKMARK:
                scoutReport.setIsBookmark(!Objects.isNull(scoutReport.getIsBookmark()) && !scoutReport.getIsBookmark());
                break;
            default:
                return;
        }
        scoutReportRepository.save(scoutReport);
    }

    @Transactional
    public void deleteReports(DeleteScoutReportCommand command) {
        List<ScoutReport> scoutReportList = scoutReportRepository.findAllById(command.getIds());
        if (command.getIds().size() != scoutReportList.size()) {
            throw KOSException.of(ErrorCode.SOME_SCOUT_REPORT_NOT_FOUND);
        }
        for (ScoutReport sr : scoutReportList) {
            if (!sr.getKosProfile().getId().equals(command.getKosProfileId())) {
                throw KOSException.of(ErrorCode.SCOUT_REPORT_DOES_NOT_BELONG_TO_YOU);
            }
        }
        for (ScoutReport scoutReport : scoutReportList) {
            scoutReport.setActive(false)
                       .setCreatedAt(LocalDateTime.now());
        }
        scoutReportRepository.saveAll(scoutReportList);
    }

    public void activeScoutReport(List<ScoutReport> scoutReports) {
        LocalDateTime now = LocalDateTime.now();
        for(ScoutReport scoutReport: scoutReports) {
            scoutReport.setActive(true).setUpdatedAt(now);
        }
        scoutReportRepository.saveAll(scoutReports);
    }
}
