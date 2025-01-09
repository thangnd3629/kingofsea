package com.supergroup.kos.building.domain.model.config;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.kos.building.domain.constant.EscortShipGroupName;
import com.supergroup.kos.building.domain.constant.EscortShipType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_escort_ship_config")
@Getter
@Setter
@Accessors(chain = true)
public class EscortShipConfig extends BaseShipConfig {
    private String         thumbnail;
    private EscortShipType type;
    private Long           buildDuration;
    private Long           wood;
    private Long           stone;
    private Long           gold;
    private Long           militaryLevelRequired;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "escort_ship_group_config_id")
    private EscortShipGroupConfig escortShipGroupConfig;

    @Transient
    private EscortShipGroupName escortShipGroupName;
    @Transient
    private Double              percentRssBuild;
}
