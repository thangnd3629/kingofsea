package com.supergroup.kos.dto.battle;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AttackResponse {
    private BattleProfileResponse    battleProfile;
    private List<MotherShipResponse> motherShip;
    private List<EscortShipResponse> escortShip;
}
