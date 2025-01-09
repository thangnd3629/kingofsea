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

import com.supergroup.core.model.BaseModel;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_mother_ship_level_config")
@Getter
@Setter
@Accessors(chain = true)
public class MotherShipLevelConfig extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long                  id;
    @Column(unique = true)
    private Long                  level;
    private Long                  wood;
    private Long                  stone;
    private Long                  gold;
    private Long                  upgradeDuration;
    private Double                percentStat;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "command_building_config_id")
    private CommandBuildingConfig commandBuildingConfig;
}
