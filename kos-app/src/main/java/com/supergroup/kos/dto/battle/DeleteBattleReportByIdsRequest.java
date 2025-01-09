package com.supergroup.kos.dto.battle;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteBattleReportByIdsRequest {
    private List<Long> ids;
}
