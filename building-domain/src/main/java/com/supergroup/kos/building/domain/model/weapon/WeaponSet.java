package com.supergroup.kos.building.domain.model.weapon;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.config.WeaponSetConfig;
import com.supergroup.kos.building.domain.model.config.WeaponSetLevelConfig;
import com.supergroup.kos.building.domain.model.ship.MotherShip;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_weapon_set")
@Getter
@Setter
@Accessors(chain = true)
public class WeaponSet extends BaseWeapon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long                 id;
    @ManyToOne
    @JoinColumn(name = "assets_id")
    private Assets               assets;
    @ManyToOne
    @JoinColumn(name = "weapon_set_level_config_id")
    private WeaponSetLevelConfig weaponSetLevelConfig;
    @ManyToOne
    @JoinColumn(name = "weapon_set_config_id")
    private WeaponSetConfig weaponSetConfig;
    @ManyToOne
    @JoinColumn(name = "mother_ship_id")
    private MotherShip      motherShip;


}
