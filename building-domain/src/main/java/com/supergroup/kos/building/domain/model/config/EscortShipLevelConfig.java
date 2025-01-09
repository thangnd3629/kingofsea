package com.supergroup.kos.building.domain.model.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.constant.TechnologyCode;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_escort_ship_level_config")
@Getter
@Setter
@Accessors(chain = true)
public class EscortShipLevelConfig extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long           id;
    private Long           level;
    private Long           wood;
    private Long           stone;
    private Long           gold;
    private Long           upgradeDuration; // millis
    private Double         percentStat;
    private EscortShipType type;
    @Enumerated(EnumType.STRING)
    private TechnologyCode technologyCodeRequirement;
}
