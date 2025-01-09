package com.supergroup.kos.building.domain.model.battle.logic;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.supergroup.kos.building.domain.model.battle.BattleUnit;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Attack implements Serializable {
    private Integer                       totalShip             = 0;
    private String                        battleFieldInfo;
    // joined
    private Map<Long, Belligerent>        belligerentJoined; // key = battleProfileId;
    //static data
    private Long                          totalMotherShipJoined = 0L;
    private Long                          totalEscortShipJoined = 0L;
    private Long                          totalAtk1Dealt        = 0L;
    private Long                          totalAtk2Dealt        = 0L;
    private Long                          totalAtk1Taken        = 0L;
    private Long                          totalAtk2Taken        = 0L;
    private Long                          totalEscortShipKilled = 0L;
    private Long                          totalMotherShipLost   = 0L;
    private Long                          totalHpMotherShipLost = 0L;
    private Long                          npcLostHp             = 0L; // hpBossLost in Pve combat
    // shipLineUp
    private Map<String, List<BattleUnit>> battleFields;
}
