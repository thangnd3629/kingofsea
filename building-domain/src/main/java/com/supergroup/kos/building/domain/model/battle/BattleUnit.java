package com.supergroup.kos.building.domain.model.battle;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @Type(value = MotherShipBattle.class, name = "MOTHER_SHIP"),
        @Type(value = EscortShipBattle.class, name = "ESCORT_SHIP"),
        @Type(value = BossSeaBattle.class, name = "BOSS"),
})
public abstract class BattleUnit implements Serializable {
    private Long    modelId;
    private Long    lineupId;
    private Long    battleProfileId;
    private Long    atk1;
    private Long    atk2;
    private Double  def1;
    private Double  def2;
    private Long    hp; // maxHp for one Ship
    private Long    kosProfileId;
    private Long    killed;
    private Long    currentHp;
    //    private Long    hpReserve;
    private Boolean isTookDamage     = false;
    private Integer amount; // total ship in round
    private Integer fighting         = 0; // ship fighting
    private Integer reserve; // ship reserve
    // for test
    private Long    totalAtk1Dealt   = 0L;
    private Long    totalAtk2Dealt   = 0L;
    private Long    totalAtk1Taken   = 0L;
    private Long    totalAtk2Taken   = 0L;
    private Long    atk1Redundant    = 0L;
    private Long    atk2Redundant    = 0L;
    @JsonIgnore
    private Long    hpLostAfterRound = 0L;
    private Long    fleetId;

    public Integer getReserve() {
        return this.amount - this.fighting;
    }

    //    @JsonIgnore
    public Integer getKilled() {
        Integer shipLiveAfterBattle = currentHp == 0 ? 0 : (int) Math.ceil(currentHp / this.getHp().doubleValue());
        return Math.max((getFighting() - shipLiveAfterBattle), 0);
    }

    public Double getDef1() {
        return def1 >= 0 ? (def1 < 1 ? def1 : 1D) : 0;
    }

    public Double getDef2() {
        return def2 >= 0 ? (def2 < 1 ? def2 : 1D) : 0;
    }

}
