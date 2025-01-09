package com.supergroup.kos.building.domain.model.config;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_queen_building_config", indexes = @Index(columnList = "level"))
@Getter
@Setter
@Accessors(chain = true)
public class QueenBuildingConfig extends BaseBuildingConfig {
    private Long maxQueen;
    private Long queenCardReward;
}
