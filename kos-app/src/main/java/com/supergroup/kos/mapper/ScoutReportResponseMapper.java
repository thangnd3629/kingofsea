package com.supergroup.kos.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.supergroup.kos.building.domain.model.scout.ScoutReport;
import com.supergroup.kos.dto.scout.ScoutReportResponse;

@Mapper
public interface ScoutReportResponseMapper {
    @Mapping(target = "timeDone", source = ".", qualifiedByName = "toTimeDone")
    @Mapping(target = "name", source = "infoElementTarget.islandName")
    @Mapping(target = "location.x", source = "infoElementTarget.coordinates.x")
    @Mapping(target = "location.y", source = "infoElementTarget.coordinates.y")
    ScoutReportResponse toResponse(ScoutReport scoutReport);
    List<ScoutReportResponse> toResponses(Iterable<ScoutReport> scoutReports);

    @Named("toTimeDone")
    default LocalDateTime toTimeDone(ScoutReport scoutReport) {
        return scoutReport.getTimeStart().plusSeconds(scoutReport.getMissionTime());
    }
}
