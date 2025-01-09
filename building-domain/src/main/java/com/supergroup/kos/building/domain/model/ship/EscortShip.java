package com.supergroup.kos.building.domain.model.ship;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.kos.building.domain.model.config.EscortShipConfig;
import com.supergroup.kos.building.domain.model.seamap.Ship;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_escort_ship")
@Getter
@Setter
@Accessors(chain = true)
public class EscortShip extends Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private Long level;
    private Long amount;

    @ManyToOne
    @JoinColumn(name = "upgrade_session_id")
    private UpgradeSession upgradeSession;

    @ManyToOne
    @JoinColumn(name = "build_session_id")
    private UpgradeSession buildSession;

    private Long          numberOfShipBuilding;
    private Boolean       inBuildQueue;
    private LocalDateTime startQueueTime;
    private Double        percentRssBuild   = 1D;
    private Double        percentSpeedBuild = 1D;
    private Long          maxLevel          = 1L;

    @ManyToOne
    @JoinColumn(name = "escort_ship_config_id")
    private EscortShipConfig escortShipConfig;
    @ManyToOne
    @JoinColumn(name = "escort_ship_group_id")
    private EscortShipGroup  escortShipGroup;

    @Transient
    private Double percentLevelStat;

    @Transient
    private Double percentQualityStat;

}
