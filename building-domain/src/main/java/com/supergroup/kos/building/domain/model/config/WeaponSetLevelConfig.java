package com.supergroup.kos.building.domain.model.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.WeaponSetLevel;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_weapon_set_level_config")
@Getter
@Setter
@Accessors(chain = true)
public class WeaponSetLevelConfig extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long                 id;
    @Column(columnDefinition = "int default 0")
    private Long                 gold;
    @Column(columnDefinition = "int default 0")
    private Long                 wood;
    @Column(columnDefinition = "int default 0")
    private Long                 stone;
    @Column(unique = true)
    private WeaponSetLevel       level;
    private Double               percentStat;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "armory_building_config_id")
    private ArmoryBuildingConfig armoryBuildingConfig;
    @Transient
    private Long                 armoryLevelRequired;
}
