package com.supergroup.kos.building.domain.model.battle;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.supergroup.core.model.BaseModel;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_battle_round", indexes = @Index(columnList = "index"))
@Getter
@Setter
@Accessors(chain = true)
public class BattleRound extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long   id;
    // index round in battle
    private Long   index;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "battle_id")
    private Battle battle;

    @OneToOne(mappedBy = "round", fetch = FetchType.LAZY)
    private RoundReport report;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "battle_round_snapshot_id")
    private BattleRoundSnapshot battleRoundSnapshot;

}
