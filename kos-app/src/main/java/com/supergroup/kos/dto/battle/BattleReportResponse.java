package com.supergroup.kos.dto.battle;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.supergroup.kos.building.domain.constant.battle.BattleResult;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.dto.seamap.CoordinatesDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BattleReportResponse {
    private Long                  id;
    private String                type;
    private String                resourceType;
    private BattleProfileResponse attacker;
    private BattleProfileResponse defender;
    private Long                  winner;
    private Long                  loser;
    private BattleResult          result;
    private CoordinatesDTO        coordinates;
    private BattleStatus          status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime         startAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime         endAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime         updatedAt;
    private Long                  amountRound;
    private List<Long>            roundIds;
    private FinalReportResponse   finalReport;
}
