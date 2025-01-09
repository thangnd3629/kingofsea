package com.supergroup.kos.dto.battle;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DamageRoundReportResponse {
    private Long amountEscortShip;
    private Long amountMotherShip;
    private Long physicalAttack;
    private Long firePower;
    private Long armour;
    private Long fireResistance;
    private Long heathPoint;
    private Long dodge;
    private Long physicalAttackTaken;
    private Long firePowerTaken;
    private Long escortShipLost;
    private Long motherShipHpLost;
    private Long takenPhysicalAttack;
    private Long takenFirePower;
}
