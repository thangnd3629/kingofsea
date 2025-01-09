package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.constant.TypeUpdateScoutReport;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpdateScoutReportCommand {
    private Long                  kosProfileId;
    private Long                  scoutReportId;
    private TypeUpdateScoutReport type;
}
