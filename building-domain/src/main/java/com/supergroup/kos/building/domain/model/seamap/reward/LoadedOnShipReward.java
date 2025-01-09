package com.supergroup.kos.building.domain.model.seamap.reward;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.supergroup.kos.building.domain.model.item.Item;
import com.supergroup.kos.building.domain.model.queen.Queen;
import com.supergroup.kos.building.domain.model.relic.Relic;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.weapon.Weapon;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_loaded_on_ship_reward")
@Getter
@Setter
@Accessors(chain = true)
public class LoadedOnShipReward {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long        id;
    @Column(columnDefinition = "float8 default 0")
    private Double      gold   = 0.D;
    @Column(columnDefinition = "float8 default 0")
    private Double      wood   = 0.D;
    @Column(columnDefinition = "float8 default 0")
    private Double      stone  = 0.D;
    @ManyToMany
    @JoinTable(
            name = "tbl_collected_reward_relic",
            joinColumns = @JoinColumn(name = "collected_reward_id"),
            inverseJoinColumns = @JoinColumn(name = "relic_id")
    )
    private List<Relic> relics = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "tbl_collected_reward_queen",
            joinColumns = @JoinColumn(name = "collected_reward_id"),
            inverseJoinColumns = @JoinColumn(name = "queen_id")
    )
    private List<Queen> queens = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "tbl_collected_reward_weapon",
            joinColumns = @JoinColumn(name = "collected_reward_id"),
            inverseJoinColumns = @JoinColumn(name = "weapon_id")
    )
    private List<Weapon> weapons = new ArrayList<>();
    @ManyToMany
    @JoinTable(
            name = "tbl_collected_reward_item",
            joinColumns = @JoinColumn(name = "collected_reward_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item>   items   = new ArrayList<>();
    @OneToOne
    @JoinColumn(name = "sea_activity_id")
    private SeaActivity  activity;
    @Column(columnDefinition = "float8 default 0")
    private Double       tonnage;

    public Double getRemainingTonnage() {
        return tonnage - gold - stone - wood;
    }

    @OneToMany
    @JoinColumn(name = "unpaid_tax_id")
    List<SeaReward> unPaidTax = new ArrayList<>();
}
