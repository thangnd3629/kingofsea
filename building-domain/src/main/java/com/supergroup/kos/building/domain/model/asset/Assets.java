package com.supergroup.kos.building.domain.model.asset;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.core.model.BaseModel;
import com.supergroup.core.utils.RoundUtil;
import com.supergroup.kos.building.domain.model.item.UserItem;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.ship.EscortShipGroup;
import com.supergroup.kos.building.domain.model.weapon.Weapon;
import com.supergroup.kos.building.domain.model.weapon.WeaponSet;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_assets")
@Getter
@Setter
@Accessors(chain = true)
public class Assets extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private Double gold;
    private Double wood;
    private Double stone;

    @Transient
    private Double totalPeople; // all people

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kos_profile_id")
    private KosProfile                  kosProfile;
    @OneToMany(mappedBy = "assets")
    private Collection<Weapon>          weapons          = new ArrayList<>();
    @OneToMany(mappedBy = "assets")
    private Collection<WeaponSet>       weaponSets       = new ArrayList<>();
    @OneToMany(mappedBy = "assets")
    private Collection<EscortShipGroup> escortShipGroups = new ArrayList<>();
    @OneToMany(mappedBy = "asset")
    private Collection<UserItem>        userItems        = new ArrayList<>();

    public Assets setGold(Double gold) {
        this.gold = RoundUtil.roundDouble(gold);
        return this;
    }

    public Assets setWood(Double wood) {
        this.wood = RoundUtil.roundDouble(wood);
        return this;
    }

    public Assets setStone(Double stone) {
        this.stone = RoundUtil.roundDouble(stone);
        return this;
    }
}
