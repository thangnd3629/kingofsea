package com.supergroup.kos.building.domain.model.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.battle.FactionType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "tbl_round_report")
public class RoundReport extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "battle_round_id")
    private BattleRound round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "battle_report_id")
    private BattleReport battleReport;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "attacker_damage_report_id", referencedColumnName = "id")
    private DamageReport attackerDamageReport;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "defender_damage_report_id", referencedColumnName = "id")
    private DamageReport defenderDamageReport;

    @OneToMany(mappedBy = "roundReport", cascade = CascadeType.PERSIST)
    private List<MotherShipReport> motherShipReports         = new ArrayList<>();
    @Transient
    private List<MotherShipReport> attackerMotherShipReports = new ArrayList<>();
    @Transient
    private List<MotherShipReport> defenderMotherShipReports = new ArrayList<>();

    @OneToMany(mappedBy = "roundReport", cascade = CascadeType.PERSIST)
    private List<BossReport> defenderBossReports = new ArrayList<>();

    @OneToMany(mappedBy = "roundReport", cascade = CascadeType.PERSIST)
    private List<EscortShipReport> escortShipReports         = new ArrayList<>();
    @Transient
    private List<EscortShipReport> attackerEscortShipReports = new ArrayList<>();
    @Transient
    private List<EscortShipReport> defenderEscortShipReports = new ArrayList<>();

    @OneToMany(mappedBy = "roundReport", cascade = CascadeType.PERSIST)
    private List<RoundUsedItem> usedItem         = new ArrayList<>();
    @Transient
    private List<RoundUsedItem> attackerUsedItem = new ArrayList<>();
    @Transient
    private List<RoundUsedItem> defenderUsedItem = new ArrayList<>();

    @OneToMany(mappedBy = "roundReport", cascade = CascadeType.PERSIST)
    private List<ReverseMotherShipReport> reserveMotherShipReports         = new ArrayList<>();
    @Transient
    private List<ReverseMotherShipReport> attackerReserveMotherShipReports = new ArrayList<>();
    @Transient
    private List<ReverseMotherShipReport> defenderReserveMotherShipReports = new ArrayList<>();

    @OneToMany(mappedBy = "roundReport", cascade = CascadeType.PERSIST)
    private List<ReverseEscortShipReport> reserveEscortShipReports         = new ArrayList<>();
    @Transient
    private List<ReverseEscortShipReport> attackerReserveEscortShipReports = new ArrayList<>();
    @Transient
    private List<ReverseEscortShipReport> defenderReserveEscortShipReports = new ArrayList<>();

    @PrePersist
    @PreUpdate
    private void prePersistOrUpdate() {
        // mother ship report
//        attackerMotherShipReports.forEach(v -> v.setFaction(FactionType.ATTACKER));
//        defenderMotherShipReports.forEach(v -> v.setFaction(FactionType.DEFENDER));
        motherShipReports.addAll(attackerMotherShipReports);
        motherShipReports.addAll(defenderMotherShipReports);
        // escort ship report
//        attackerEscortShipReports.forEach(v -> v.setFaction(FactionType.ATTACKER));
//        defenderEscortShipReports.forEach(v -> v.setFaction(FactionType.DEFENDER));
        escortShipReports.addAll(attackerEscortShipReports);
        escortShipReports.addAll(defenderEscortShipReports);
        // reserve mother ship report
//        attackerReserveMotherShipReports.forEach(v -> v.setFaction(FactionType.ATTACKER));
//        defenderReserveMotherShipReports.forEach(v -> v.setFaction(FactionType.DEFENDER));
        reserveMotherShipReports.addAll(attackerReserveMotherShipReports);
        reserveMotherShipReports.addAll(defenderReserveMotherShipReports);
        // reserve escort ship report
//        attackerReserveEscortShipReports.forEach(v -> v.setFaction(FactionType.ATTACKER));
//        defenderReserveEscortShipReports.forEach(v -> v.setFaction(FactionType.DEFENDER));
        reserveEscortShipReports.addAll(attackerReserveEscortShipReports);
        reserveEscortShipReports.addAll(defenderReserveEscortShipReports);
        // used item
        attackerUsedItem.forEach(v -> v.setFaction(FactionType.ATTACKER));
        defenderUsedItem.forEach(v -> v.setFaction(FactionType.DEFENDER));
        usedItem.addAll(attackerUsedItem);
        usedItem.addAll(defenderUsedItem);
    }

    @PostLoad
    private void postLoad() {
        attackerMotherShipReports = motherShipReports.stream()
                                                     .filter(v -> v.getFaction().equals(FactionType.ATTACKER))
                                                     .collect(Collectors.toList());
        defenderMotherShipReports = motherShipReports.stream()
                                                     .filter(v -> v.getFaction().equals(FactionType.DEFENDER))
                                                     .collect(Collectors.toList());
        attackerEscortShipReports = escortShipReports.stream()
                                                     .filter(v -> v.getFaction().equals(FactionType.ATTACKER))
                                                     .collect(Collectors.toList());
        defenderEscortShipReports = escortShipReports.stream()
                                                     .filter(v -> v.getFaction().equals(FactionType.DEFENDER))
                                                     .collect(Collectors.toList());
        attackerReserveMotherShipReports = reserveMotherShipReports.stream()
                                                                   .filter(v -> v.getFaction().equals(FactionType.ATTACKER))
                                                                   .collect(Collectors.toList());
        defenderReserveMotherShipReports = reserveMotherShipReports.stream()
                                                                   .filter(v -> v.getFaction().equals(FactionType.DEFENDER))
                                                                   .collect(Collectors.toList());
        attackerReserveEscortShipReports = reserveEscortShipReports.stream()
                                                                   .filter(v -> v.getFaction().equals(FactionType.ATTACKER))
                                                                   .collect(Collectors.toList());
        defenderReserveEscortShipReports = reserveEscortShipReports.stream()
                                                                   .filter(v -> v.getFaction().equals(FactionType.DEFENDER))
                                                                   .collect(Collectors.toList());
        attackerUsedItem = usedItem.stream()
                                   .filter(v -> v.getFaction().equals(FactionType.ATTACKER))
                                   .collect(Collectors.toList());
        defenderUsedItem = usedItem.stream()
                                   .filter(v -> v.getFaction().equals(FactionType.DEFENDER))
                                   .collect(Collectors.toList());
    }
}
