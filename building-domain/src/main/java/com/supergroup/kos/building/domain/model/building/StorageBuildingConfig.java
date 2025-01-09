package com.supergroup.kos.building.domain.model.building;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.supergroup.kos.building.domain.constant.StorageType;
import com.supergroup.kos.building.domain.model.config.BaseBuildingConfig;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_storage_building_config")
@Getter
@Setter
@Accessors(chain = true)
public class StorageBuildingConfig extends BaseBuildingConfig {
    private StorageType type;
    private Double      capacity;
    private Long        levelHeadquarters;
}
