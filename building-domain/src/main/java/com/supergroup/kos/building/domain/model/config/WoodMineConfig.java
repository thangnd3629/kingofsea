package com.supergroup.kos.building.domain.model.config;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_wood_mine_config", indexes = @Index(columnList = "level"))
@Getter
@Setter
@Accessors(chain = true)
public class WoodMineConfig extends BaseBuildingConfig {
    private Long   researchLevelRequired;
    private Long   maxWorker;
    private Double woodPerWorker; // seconds
}
