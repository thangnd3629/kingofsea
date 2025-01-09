package com.supergroup.kos.building.domain.command;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DeleteScoutReportCommand {
    private Long       kosProfileId;
    private List<Long> ids;
}
