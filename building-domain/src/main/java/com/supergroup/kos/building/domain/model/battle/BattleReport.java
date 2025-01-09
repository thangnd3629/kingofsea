package com.supergroup.kos.building.domain.model.battle;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.seamap.ResourceIslandType;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "tbl_battle_report")
public class BattleReport extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    private ResourceIslandType resourceType; // use if mining combat

    @Embedded
    Coordinates coordinates;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    private BattleProfile initiator;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "victim_id")
    private BattleProfile victim;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private BattleProfile winner;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loser_id")
    private BattleProfile loser;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "battle_id")
    private Battle battle;

    @OneToMany(mappedBy = "battleReport")
    private List<BattleProfile> joiners;

    @OneToMany(mappedBy = "battleReport", cascade = CascadeType.PERSIST)
    private List<RoundReport> roundReports;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "battle_reward_id")
    private BattleReward reward;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "totalAtk1", column = @Column(name = "attacker_totalAtk1")),
            @AttributeOverride(name = "totalAtk2", column = @Column(name = "attacker_totalAtk2")),
            @AttributeOverride(name = "takenAtk1", column = @Column(name = "attacker_takenAtk1")),
            @AttributeOverride(name = "takenAtk2", column = @Column(name = "attacker_takenAtk2")),
            @AttributeOverride(name = "escortShipLost", column = @Column(name = "attacker_escortShipLost")),
            @AttributeOverride(name = "motherShipHpLost", column = @Column(name = "attacker_motherShipHpLost")),
            @AttributeOverride(name = "motherShipDied", column = @Column(name = "attacker_motherShipDied")),
            @AttributeOverride(name = "amountAlly", column = @Column(name = "attacker_amountAlly")),
            @AttributeOverride(name = "amountItem", column = @Column(name = "attacker_amountItem")),
            @AttributeOverride(name = "npcLostHp", column = @Column(name = "attacker_npcLostHp")),
    })
    private BattleFinalReport attackerFinalReport;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "totalAtk1", column = @Column(name = "defender_totalAtk1")),
            @AttributeOverride(name = "totalAtk2", column = @Column(name = "defender_totalAtk2")),
            @AttributeOverride(name = "takenAtk1", column = @Column(name = "defender_takenAtk1")),
            @AttributeOverride(name = "takenAtk2", column = @Column(name = "defender_takenAtk2")),
            @AttributeOverride(name = "escortShipLost", column = @Column(name = "defender_escortShipLost")),
            @AttributeOverride(name = "motherShipHpLost", column = @Column(name = "defender_motherShipHpLost")),
            @AttributeOverride(name = "motherShipDied", column = @Column(name = "defender_motherShipDied")),
            @AttributeOverride(name = "amountAlly", column = @Column(name = "defender_amountAlly")),
            @AttributeOverride(name = "amountItem", column = @Column(name = "defender_amountItem")),
            @AttributeOverride(name = "npcLostHp", column = @Column(name = "defender_npcLostHp")),
    })
    private BattleFinalReport defenderFinalReport;

//    @Override
//    public BigInteger getBattleId() {
//        return BigInteger.valueOf(battle.getId());
//    }
//
//    @Override
//    public BattleType getBattleType() {
//        return battle.getBattleType();
//    }
//
//    @Override
//    public BigInteger getAttackerKosProfileId() {
//        return BigInteger.valueOf(initiator.getKosProfile().getId());
//    }
//
//    @Override
//    public BattleProfileType getAttackerType() {
//        return initiator.getType();
//    }
//
//    @Override
//    public BigInteger getAttackerX() {
//        return BigInteger.valueOf(initiator.getCoordinates().getX());
//    }
//
//    @Override
//    public BigInteger getAttackerY() {
//        return BigInteger.valueOf(initiator.getCoordinates().getY());
//    }
//
//    @Override
//    public String getAttackerName() {
//        return initiator.getUsername();
//    }
//
//    @Override
//    public String getAttackerAvatarUrl() {
//        return initiator.getAvatar();
//    }
//
//    @Override
//    public BattleProfileType getDefenderType() {
//        return victim.getType();
//    }
//
//    @Override
//    public BigInteger getDefenderKosProfileId() {
//        return BigInteger.valueOf(victim.getKosProfile().getId());
//    }
//
//    @Override
//    public BigInteger getDefenderX() {
//        return BigInteger.valueOf(victim.getCoordinates().getX());
//    }
//
//    @Override
//    public BigInteger getDefenderY() {
//        return BigInteger.valueOf(victim.getCoordinates().getY());
//    }
//
//    @Override
//    public String getDefenderName() {
//        return victim.getUsername();
//    }
//
//    @Override
//    public String getDefenderAvatarUrl() {
//        return victim.getAvatar();
//    }
//
//    @Override
//    public BigInteger getDefenderBossId() {
//        return BigInteger.valueOf(victim.getBossSea().getId());
//    }
//
//    @Override
//    public BigInteger getDefenderBossConfigId() {
//        return BigInteger.valueOf(victim.getBossSea().getConfigId());
//    }
//
//    @Override
//    public BigInteger getWinnerId() {
//        return BigInteger.valueOf(winner.getId());
//    }
//
//    @Override
//    public BigInteger getLoserId() {
//        return BigInteger.valueOf(loser.getId());
//    }
//
//    @Override
//    public BigInteger getX() {
//        return BigInteger.valueOf(coordinates.getX());
//    }
//
//    @Override
//    public BigInteger getY() {
//        return BigInteger.valueOf(coordinates.getY());
//    }
//
//    @Override
//    public BattleStatus getStatus() {
//        return battle.getStatus();
//    }
//
//    @Override
//    public BigInteger getAmountRound() {
//        return BigInteger.valueOf(battle.getBattleRounds().size());
//    }
//
//    @Override
//    public BattleProfile victimProfile() {
//        return victim;
//    }
//
//    @Override
//    public BattleProfile initiatorProfile() {
//        return initiator;
//    }
//
//    @Override
//    public BattleProfile winnerProfile() {
//        return winner;
//    }
//
//    @Override
//    public BattleProfile loserProfile() {
//        return loser;
//    }
}
