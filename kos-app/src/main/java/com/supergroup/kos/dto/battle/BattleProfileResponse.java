package com.supergroup.kos.dto.battle;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.supergroup.kos.building.domain.constant.BattleProfileType;
import com.supergroup.kos.building.domain.constant.battle.BattleType;
import com.supergroup.kos.building.domain.dto.seamap.CoordinatesDTO;
import com.supergroup.kos.building.domain.dto.seamap.MoveSessionDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleProfileResponse {
    private BattleProfileType type;
    private Long              userId;
    private BattleType        battleType;
    private Long              kosProfileId;
    private String            battleFieldName;
    private String            username;
    private String            avatarUrl;
    private CoordinatesDTO    coordinates;
    private MoveSessionDTO    moveSession;
    private Long              bossId;
    private Long              bossConfigId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime     startAt;
    private CoordinatesDTO    battleFieldCoordinates;
    private Long              prepareDuration;
}
