package com.supergroup.kos.building.domain.model.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.EscortShipGroupLevel;
import com.supergroup.kos.building.domain.constant.EscortShipGroupName;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_escort_ship_group_level_config")
@Getter
@Setter
@Accessors(chain = true)
public class EscortShipGroupLevelConfig extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long                  id;
    private EscortShipGroupLevel  level;
    private Long                  wood;
    private Long                  stone;
    private Long                  gold;
    private String                description;
    private Double                percentStat;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "escort_ship_group_config_id")
    private EscortShipGroupConfig escortShipGroupConfig;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "armory_building_config_id")
    private ArmoryBuildingConfig  armoryBuildingConfig;
    @Transient
    private Long                  armoryLevelRequired;
    @Transient
    private EscortShipGroupName   shipGroupName;

}
