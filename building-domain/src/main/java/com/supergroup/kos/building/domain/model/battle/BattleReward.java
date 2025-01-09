package com.supergroup.kos.building.domain.model.battle;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.supergroup.kos.building.domain.model.item.Item;
import com.supergroup.kos.building.domain.model.queen.Queen;
import com.supergroup.kos.building.domain.model.relic.Relic;
import com.supergroup.kos.building.domain.model.seamap.reward.SeaReward;
import com.supergroup.kos.building.domain.model.weapon.Weapon;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "tbl_battle_reward")
@DiscriminatorValue("BATTLE")
public class BattleReward extends SeaReward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long         id;
    @OneToOne(mappedBy = "reward")
    private BattleReport battleReport;
    private Double       gold       = 0.0;
    private Double       stone      = 0.0;
    private Double       wood       = 0.0;
    private Double       gloryPoint = 0.0;

    private Double goldRemaining  = 0.0D;
    private Double woodRemaining  = 0.0D;
    private Double stoneRemaining = 0.0D;

    @ManyToMany
    @JoinTable(
            name = "tbl_battle_reward_relic",
            joinColumns = @JoinColumn(name = "battle_reward_id"),
            inverseJoinColumns = @JoinColumn(name = "relic_id")
    )
    private List<Relic> relics         = new ArrayList<>();
    private Boolean     isRelicsLoaded = false;

    @ManyToMany
    @JoinTable(
            name = "tbl_battle_reward_queen",
            joinColumns = @JoinColumn(name = "battle_reward_id"),
            inverseJoinColumns = @JoinColumn(name = "queen_id")
    )
    private List<Queen> queens        = new ArrayList<>();
    private Boolean     isQueenLoaded = false;

    @ManyToMany
    @JoinTable(
            name = "tbl_battle_reward_item",
            joinColumns = @JoinColumn(name = "battle_reward_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items        = new ArrayList<>();
    private Boolean    isItemLoaded = false;

    @ManyToMany
    @JoinTable(
            name = "tbl_battle_reward_weapon",
            joinColumns = @JoinColumn(name = "battle_reward_id"),
            inverseJoinColumns = @JoinColumn(name = "weapon_id")
    )
    private List<Weapon> weapons        = new ArrayList<>();
    private Boolean      isWeaponLoaded = false;
}
