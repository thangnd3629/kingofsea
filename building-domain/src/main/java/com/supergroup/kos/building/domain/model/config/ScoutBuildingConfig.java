package com.supergroup.kos.building.domain.model.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_scout_building_config")
@Getter
@Setter
@Accessors(chain = true)
public class ScoutBuildingConfig extends BaseBuildingConfig {
    private Long castleLevelRequired;
    private Long capacity;

    private Long costTrainingWood;
    private Long costTrainingStone;
    private Long costTrainingGold;
    private Long trainingTime;
}
