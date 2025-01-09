package com.supergroup.kos.building.domain.model.battle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
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

import com.supergroup.core.converter.LongBlobConverter;
import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.battle.BattleCancelReason;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.constant.battle.BattleType;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_battle")
@Getter
@Setter
@Accessors(chain = true)
public class Battle extends BaseModel {
    @Id
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long          id;
    private Long          currentRound;
    private LocalDateTime timeUpdateStatus;

    private BattleType         battleType;
    @Enumerated(EnumType.STRING)
    private BattleCancelReason cancelReason;
    @Enumerated(EnumType.STRING)
    private BattleStatus       status;
    @Embedded
    private Coordinates        battleSite;
    @Embedded
    private CheckResultBattle  checkResult;

    @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "battle_profile_attacker_id")
    private BattleProfile attacker;

    @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "battle_profile_defender_id")
    private BattleProfile defender;

    @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "battle_profile_winner_id")
    private BattleProfile winner;

    @OneToOne(mappedBy = "battle", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private BattleReport battleReport;

    @OneToMany(mappedBy = "battle", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<BattleRound> battleRounds = new ArrayList<>();

//    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ShipLineUp> shipLineUps = new ArrayList<>();

    @OneToMany(mappedBy = "battle", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<BattleProfile> battleProfiles = new ArrayList<>();
    @OneToOne(mappedBy = "battle", fetch = FetchType.LAZY)
    private SeaElement          battleField;
    @Convert(converter = LongBlobConverter.class)
    private List<Long>        lineUpIdsWithdrawNextRounds  = new ArrayList<>();
    private Boolean                   attackerWithdrawAllNextRound = false;

    public Battle setStatus(BattleStatus status) {
        this.status = status;
        this.timeUpdateStatus = LocalDateTime.now();
        return this;
    }

    public void cancel(BattleCancelReason reason) {
        this.status = BattleStatus.CANCEL;
        this.cancelReason = reason;
    }

    public BattleProfile getLoser() {
        if (Objects.isNull(winner)) {
            return null;
        }
        if (defender.getId().equals(winner.getId())) {
            return attacker;
        }
        return defender;
    }

}
