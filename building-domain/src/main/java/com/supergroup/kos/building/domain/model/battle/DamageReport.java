package com.supergroup.kos.building.domain.model.battle;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Include "total" information in the round
 */
@Getter
@Setter
@Entity
@Table(name = "tbl_damage_report")
@Accessors(chain = true)
public class DamageReport implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private Long escortShip          = 0L; // totalEscortShipJoined
    private Long motherShip          = 0L; // totalMotherShipJoined
    private Long physicalAttack      = 0L; // totalAtk1Dealt
    private Long firePower           = 0L; // totalAtk2Dealt
    private Long armour; // todo remote
    private Long fireResistance; // todo remote
    private Long heathPoint; // todo remote
    private Long dodge; // todo remote
    private Long takenPhysicalAttack = 0L; // totalAtk1Taken
    private Long takenFirePower      = 0L; // totalAtk2Taken
    private Long escortShipLost      = 0L; // totalEscortShipKilled
    private Long motherShipLost      = 0L; //totalMotherShipLost
    private Long npcLostHp           = 0L; // hpBossLost in Pve combat

    //
    private Long totalMotherShipJoined;
    private Long totalEscortShipJoined;
    private Long totalAtk1Dealt;
    private Long totalAtk2Dealt;
    private Long totalAtk1Taken;
    private Long totalAtk2Taken;
    private Long totalEscortShipKilled;
    private Long totalMotherShipLost;
    private Long totalHpMotherShipLost;
}
