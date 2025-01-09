package com.supergroup.kos.dto.scout;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteScoutReportRequest {
    private List<Long> ids;
}
