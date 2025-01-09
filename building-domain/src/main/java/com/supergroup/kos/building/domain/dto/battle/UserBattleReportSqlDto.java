package com.supergroup.kos.building.domain.dto.battle;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBattleReportSqlDto {
    private Long   battleId;
    private Long   battleType;
    private String attackerType;

    // attacker
    private Long   attackerKosProfileId;
    private Long   attackerX;
    private Long   attackerY;
    private String attackerName;
    private String attackerAvatarUrl;

    // defender
    private String defenderType;
    private Long   defenderKosProfileId;
    private Long   defenderX;
    private Long   defenderY;
    private String defenderName;
    private String defenderAvatarUrl;
    private Long   defenderBossId;
    private Long   defenderBossX;
    private Long   defenderBossY;
    private Long   defenderElementConfigId;
    private Long   defenderBossName;
    private String defenderBossAvatarUrl;

    private Long winner;
    private Long loser;

    private String        status;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime updatedAt;
    private Long          amountRound;
}
