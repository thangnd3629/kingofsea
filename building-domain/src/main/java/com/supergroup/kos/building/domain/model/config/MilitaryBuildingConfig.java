package com.supergroup.kos.building.domain.model.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_military_building_config")
@Getter
@Setter
@Accessors(chain = true)
public class MilitaryBuildingConfig extends BaseBuildingConfig {
    private Long researchLevelRequired;
    private Double percentDurationBuildShip;
}
