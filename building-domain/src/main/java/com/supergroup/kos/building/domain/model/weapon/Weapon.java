package com.supergroup.kos.building.domain.model.weapon;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.core.constant.BaseStatus;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.config.WeaponConfig;
import com.supergroup.kos.building.domain.model.ship.MotherShip;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_weapon")
@Getter
@Setter
@Accessors(chain = true)
public class Weapon extends BaseWeapon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long         id;
    @ManyToOne
    @JoinColumn(name = "assets_id")
    private Assets       assets;
    @ManyToOne
    @JoinColumn(name = "weapon_config_id")
    private WeaponConfig weaponConfig;
    @ManyToOne
    @JoinColumn(name = "mother_ship_id")
    private MotherShip   motherShip;
    @Transient
    private Long         qualityExist;
    @Column(name = "isDeleted", columnDefinition = "boolean default false")
    private Boolean      isDeleted = false;

    private BaseStatus status;
}
