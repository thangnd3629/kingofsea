package com.supergroup.kos.building.domain.model.battle;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.battle.FactionType;
import com.supergroup.kos.building.domain.model.item.Item;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_round_used_item")
@Getter
@Setter
@Accessors(chain = true)
public class RoundUsedItem extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_report_id")
    private RoundReport roundReport;

    @ManyToMany
    @JoinTable(
            name = "tbl_used_item_round_report",
            joinColumns = @JoinColumn(name = "round_used_item_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "battle_profile_id")
    private BattleProfile battleProfile;

    @Enumerated(EnumType.STRING)
    private FactionType faction;
}
