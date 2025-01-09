package com.supergroup.kos.building.domain.model.config;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_castle_config", indexes = @Index(columnList = "level"))
@Getter
@Setter
@Accessors(chain = true)
public class CastleConfig extends BaseBuildingConfig {
    private Long instant; // cost if use kos
    private Double goldPerPerson; // seconds
    private Double mpMultiplier;
    private Double populationGrowthBase;
    private Long maxPopulation;
}
