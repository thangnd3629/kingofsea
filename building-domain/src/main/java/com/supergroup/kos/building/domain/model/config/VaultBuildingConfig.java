package com.supergroup.kos.building.domain.model.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_vault_building_config")
@Getter
@Setter
@Accessors(chain = true)

public class VaultBuildingConfig extends BaseBuildingConfig {

    private Double protectPercent;
    private Long   researchLevelRequired;

}
